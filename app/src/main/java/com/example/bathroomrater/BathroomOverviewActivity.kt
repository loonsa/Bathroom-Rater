package com.example.bathroomrater

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BathroomOverviewActivity: AppCompatActivity() {
    private lateinit var lpdHandler: LocalPersistentDataHandler
    private lateinit var ratingBar: RatingBar
    private lateinit var avgRating: TextView
    private lateinit var numReviews: TextView
    private lateinit var bathroom: Bathroom
    private lateinit var addReviewButton: Button
    private lateinit var addFavoriteButton: Button
    private lateinit var backButton: Button

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

        ratingBar = findViewById(R.id.rating_bar)
        avgRating = findViewById(R.id.avg_rating)
        numReviews = findViewById(R.id.num_reviews)

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
            numReviews.text = bathroom.numReviews.toString()
        }
    }
}