package com.example.bathroomrater

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BathroomOverviewActivity: AppCompatActivity() {
    private lateinit var lpdHandler: LocalPersistentDataHandler
    private lateinit var ratingBar: RatingBar
    private lateinit var bathroom: Bathroom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bathroom_overview)

        lpdHandler = LocalPersistentDataHandler(this)
        val bathroomId: String = intent.getStringExtra("bathroomId") ?: return
        bathroom = MainActivity.bathrooms.find { bathroom -> bathroom.uniqueId == bathroomId }

        ratingBar = findViewById(R.id.rating_bar)
        ratingBar.rating = bathroom.averageRating.toFloat()

        val addReviewButton: Button = findViewById<Button>(R.id.add_review)
        addReviewButton.setOnClickListener {
            val intent: Intent = Intent(this, AddReviewActivity::class.java)
            intent.putExtra("bathroomId", bathroom.uniqueId)
            startActivity(intent)
        }

        val addFavoriteButton: Button = findViewById<Button>(R.id.add_favorite)
        addFavoriteButton.setOnClickListener {
            if (lpdHandler.getFavorites().contains(bathroom.uniqueId)) {
                lpdHandler.removeFavorite(bathroom.uniqueId)
                addFavoriteButton.text = "Add to Favorites"
            } else {
                lpdHandler.addFavorite(bathroom.uniqueId)
                addFavoriteButton.text = "Remove from Favorites"
            }
        }
    }
}