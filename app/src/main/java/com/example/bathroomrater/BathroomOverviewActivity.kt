package com.example.bathroomrater

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BathroomOverviewActivity: AppCompatActivity() {
    private lateinit var lpdHandler: LocalPersistentDataHandler
    private lateinit var ratingBar: RatingBar
    private lateinit var avgRating: TextView
    private lateinit var numReviews: TextView
    private lateinit var bathroom: Bathroom
    private lateinit var addReviewButton: Button
    private lateinit var addFavoriteButton: Button
    private lateinit var backButton: Button
    private lateinit var reviewsBox: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bathroom_overview)
        supportActionBar?.hide()

        lpdHandler = LocalPersistentDataHandler(this)
        val bathroomId: String = intent.getStringExtra("bathroomId") ?: return
        bathroom = MainActivity.bathrooms.find { bathroom -> bathroom.uniqueId == bathroomId } ?: run {
            Log.w("BathroomOverviewActivity", "bathroom wasn't found in MainActivity")
            this.finish()
            return
        }
        lpdHandler.setLastViewedBathroomId(bathroom.uniqueId) //should be same thing as bathroomId variable

        val bathroomName: TextView = findViewById(R.id.bathroom_name)
        val buildingAndFloor: TextView = findViewById(R.id.building_and_floor)
        val genderNeutral: TextView = findViewById(R.id.gender_neutral)
        val adaAccessible: TextView = findViewById(R.id.ada_accessible)

        bathroomName.text = bathroom.name
        buildingAndFloor.text = "${bathroom.building} - Floor ${bathroom.floor}"
        if (!bathroom.isGenderNeutral && !bathroom.isADA) {
            genderNeutral.text = "None" //just use genderNeutral textView to hold None
            adaAccessible.visibility = View.GONE
        } else {
            genderNeutral.text = "Gender Neutral"
            genderNeutral.visibility = if (bathroom.isGenderNeutral) View.VISIBLE else View.GONE
            adaAccessible.visibility = if (bathroom.isADA) View.VISIBLE else View.GONE
        }

        ratingBar = findViewById(R.id.rating_bar)
        avgRating = findViewById(R.id.avg_rating)
        numReviews = findViewById(R.id.num_reviews)
        reviewsBox = findViewById(R.id.reviews_box)

        addReviewButton = findViewById<Button>(R.id.add_review)
        addReviewButton.setOnClickListener {
            val intent: Intent = Intent(this, AddReviewActivity::class.java)
            intent.putExtra("bathroomId", bathroom.uniqueId)
            startActivity(intent)
        }

        addFavoriteButton = findViewById<Button>(R.id.add_favorite)
        addFavoriteButton.setOnClickListener {
            if (lpdHandler.getFavorites().contains(bathroom.uniqueId)) {
                lpdHandler.removeFavorite(bathroom.uniqueId)
                addFavoriteButton.text = "Add to Favorites"
            } else {
                lpdHandler.addFavorite(bathroom.uniqueId)
                addFavoriteButton.text = "Remove from Favorites"
            }
        }
        if (lpdHandler.getFavorites().contains(bathroom.uniqueId)) {
            addFavoriteButton.text = "Remove from Favorites"
        } else {
            addFavoriteButton.text = "Add to Favorites"
        }

        backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener {
            this.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::bathroom.isInitialized && ::ratingBar.isInitialized &&
            ::avgRating.isInitialized && ::numReviews.isInitialized) {
            ratingBar.rating = bathroom.averageRating.toFloat()
            avgRating.text = bathroom.averageRating.toString()
            var quantifier: String = " reviews"
            if (bathroom.numReviews == 1) {
                quantifier = " review"
            }
            numReviews.text = bathroom.numReviews.toString() + quantifier

            val task = ServerTaskGetReviews(this, bathroom.uniqueId)
            task.start()
        }
    }

    fun loadReviews(reviews: ArrayList<Review>) {
        reviewsBox.removeAllViews()

        // Calculate rating from actual reviews
        if (reviews.isNotEmpty()) {
            val calculatedAvg = reviews.sumOf { it.rating } / reviews.size
            val roundedAvg = Math.round(calculatedAvg * 10) / 10.0
            ratingBar.rating = roundedAvg.toFloat()
            avgRating.text = roundedAvg.toString()
            var quantifier: String = " reviews"
            if (reviews.size == 1) {
                quantifier = " review"
            }
            numReviews.text = reviews.size.toString() + quantifier
        }

        if (reviews.isEmpty()) {
            val noReviews = TextView(this)
            noReviews.text = "No reviews yet"
            noReviews.textSize = 15f
            noReviews.setTextColor(0xFF777777.toInt())
            reviewsBox.addView(noReviews)
            return
        }

        reviews.sortByDescending { it.timeOfReview }

        for (review in reviews) {
            val card = LinearLayout(this)
            card.orientation = LinearLayout.VERTICAL
            card.setPadding(0, 0, 0, 30)

            val username = TextView(this)
            username.text = "${review.username} — ${review.rating}/5.0"
            username.textSize = 16f
            username.setTextColor(0xFF111111.toInt())
            card.addView(username)

            val comment = TextView(this)
            comment.text = review.comment
            comment.textSize = 14f
            comment.setTextColor(0xFF444444.toInt())
            card.addView(comment)

            val date = TextView(this)
            val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            date.text = sdf.format(Date(review.timeOfReview))
            date.textSize = 12f
            date.setTextColor(0xFF999999.toInt())
            card.addView(date)

            reviewsBox.addView(card)
        }
    }
}