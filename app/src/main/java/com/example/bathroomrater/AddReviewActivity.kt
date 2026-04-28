package com.example.bathroomrater

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddReviewActivity : AppCompatActivity() {

    private lateinit var ratingBar: RatingBar
    private lateinit var tvRatingLabel: TextView
    private lateinit var etComment: EditText
    private var selectedRating: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        // Get the bathroomId passed from BathroomOverviewActivity
        val bathroomId = intent.getStringExtra("bathroomId")
        if (bathroomId == null) {
            Toast.makeText(this, "Error: No bathroom selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Find the bathroom name to display at the top
        // TODO: uncomment once Member 1 adds companion object to MainActivity
        val bathroom = MainActivity.bathrooms.firstOrNull { it.uniqueId == bathroomId }
        val tvTitle = findViewById<TextView>(R.id.tv_review_title)
        tvTitle.text = "Add Review"
        //tvTitle.text = if (bathroom != null) "Review: ${bathroom.name}" else "Add Review"

        // Set up the interactive RatingBar with listener
        ratingBar = findViewById(R.id.rating_bar_input)
        tvRatingLabel = findViewById(R.id.tv_rating_label)
        etComment = findViewById(R.id.et_comment)

        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                selectedRating = rating
                tvRatingLabel.text = when {
                    rating <= 1f -> "Terrible"
                    rating <= 2f -> "Poor"
                    rating <= 3f -> "Average"
                    rating <= 4f -> "Good"
                    else -> "Excellent"
                }
            }

        // Submit button, validate then push to Firebase
        val btnSubmit = findViewById<Button>(R.id.btn_submit)
        btnSubmit.setOnClickListener {
            val comment = etComment.text.toString().trim()

            if (selectedRating == 0f) {
                Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (comment.isEmpty()) {
                Toast.makeText(this, "Please write a comment", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val review = Review(
                "",
                bathroomId,
                "anonymous",
                selectedRating.toDouble(),
                comment,
                System.currentTimeMillis()
            )

            val task = ServerTaskAddReview(review)
            task.start()

            Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Back button
        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener { finish() }
    }
}