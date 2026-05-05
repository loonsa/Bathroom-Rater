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
    private lateinit var etUsername: EditText
    private var selectedRating: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        val bathroomId = intent.getStringExtra("bathroomId")
        if (bathroomId == null) {
            Toast.makeText(this, "Error: No bathroom selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val bathroom = MainActivity.bathrooms.firstOrNull { it.uniqueId == bathroomId }
        val tvTitle = findViewById<TextView>(R.id.tv_review_title)
        tvTitle.text = "Add Review"

        ratingBar = findViewById(R.id.rating_bar_input)
        tvRatingLabel = findViewById(R.id.tv_rating_label)
        etComment = findViewById(R.id.et_comment)
        etUsername = findViewById<EditText>(R.id.et_username)

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

        val btnSubmit = findViewById<Button>(R.id.btn_submit)
        btnSubmit.setOnClickListener {
            val comment = etComment.text.toString().trim()
            val username = etUsername.text.toString().ifEmpty { "anonymous" }

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
                username,
                selectedRating.toDouble(),
                comment,
                System.currentTimeMillis()
            )

            val task = ServerTaskAddReview(this, review)
            task.start()
        }

        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener { finish() }
    }

    fun onReviewAdded() {
        val bathroomId = intent.getStringExtra("bathroomId") ?: return
        val bathroom: Bathroom? = MainActivity.bathrooms.find { bathroom -> bathroom.uniqueId == bathroomId }

        if (bathroom != null) {
            val oldTotalStars: Double = bathroom.averageRating * bathroom.numReviews
            val newTotalStars: Double = oldTotalStars + selectedRating.toDouble()

            bathroom.numReviews += 1
            bathroom.averageRating = newTotalStars / bathroom.numReviews

            val updateTask: ServerTaskUpdateBathroom = ServerTaskUpdateBathroom(this, bathroomId, bathroom.averageRating, bathroom.numReviews)
            updateTask.start()
        } else {
            finish()
        }
    }

    fun onBathroomUpdated() {
        Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show()
        finish()
    }
}