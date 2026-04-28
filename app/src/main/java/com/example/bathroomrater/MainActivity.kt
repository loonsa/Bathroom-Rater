package com.example.bathroomrater

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    companion object {
        var bathrooms: ArrayList<Bathroom> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MobileAds.initialize(this)
        val adView = findViewById<AdView>(R.id.ad_view)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // TODO: Member 1 — add Google Maps, GPS, filter switch, ServerTaskGetBathrooms here
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val prefs = LocalPersistentDataHandler(this)
        when (item.itemId) {
            R.id.sort_highest_rated -> prefs.setSortOrderPref("highest_rated")
            R.id.sort_nearest -> prefs.setSortOrderPref("nearest")
            R.id.sort_most_reviews -> prefs.setSortOrderPref("most_reviews")
        }
        // TODO: Member 1 — refresh map pins after sort change
        return true
    }

    fun loadBathrooms(bathrooms: ArrayList<Bathroom>) {
        MainActivity.bathrooms = bathrooms
        // TODO: Member 1 — refresh map pins
    }
}