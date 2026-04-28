package com.example.bathroomrater

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
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

        val addBathroomButton: Button = findViewById<Button>(R.id.add_bathroom)
        addBathroomButton.setOnClickListener {
            val intent: Intent = Intent(this, AddBathroomActivity::class.java)
            startActivity(intent)
        }

        // TODO: Member 1 — add Google Maps, GPS, filter switch, ServerTaskGetBathrooms here
        // TODO: also needs to setOnMarkerClickListener to pass the bathroomId to BathroomOverviewActivity
        // and then launch BathroomOverviewActivity
        // IMPORTANT: the bathroomId is the key for the JSON bathroom object and is also its uniqueId field
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val prefs = LocalPersistentDataHandler(this)
        when (item.itemId) {
            R.id.filter_top_rated -> prefs.setShowTopOnly(true)
            R.id.filter_favorites -> prefs.setShowFavoritesOnly(true)
            R.id.filter_gender_neutral -> prefs.setShowGenderNeutralOnly(true)
            R.id.filter_clear -> {
                prefs.setShowTopOnly(false)
                prefs.setShowFavoritesOnly(false)
                prefs.setShowGenderNeutralOnly(false)
            }
            R.id.refresh -> {
                Toast.makeText(this, "Refreshing bathroom data", Toast.LENGTH_SHORT).show()

                val refreshTask: ServerTaskGetBathrooms = ServerTaskGetBathrooms(this)
                refreshTask.start()
            }
        }
        // TODO: Member 1 — refresh map pins after sort change
        return true
    }

    fun loadBathrooms(bathrooms: ArrayList<Bathroom>) {
        MainActivity.bathrooms = bathrooms
        // TODO: Member 1 — refresh map pins
    }
}