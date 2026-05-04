package com.example.bathroomrater

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Switch
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map : GoogleMap
    private lateinit var prefs : LocalPersistentDataHandler
    
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

        prefs = LocalPersistentDataHandler(this) // saved filter data
        val filterSwitchGenderNeutralOnly: SwitchCompat = findViewById<SwitchCompat>(R.id.filter_switch_gender_neutral)
        filterSwitchGenderNeutralOnly.isChecked = prefs.filterStatus("genderNeutralOnly")
        filterSwitchGenderNeutralOnly.setOnCheckedChangeListener {_, isChecked->
            prefs.setShowGenderNeutralOnly(isChecked)
            refreshMapPins()
            }
        val filterSwitchFavorites: SwitchCompat = findViewById<SwitchCompat>(R.id.filter_switch_favorites)
        filterSwitchFavorites.isChecked = prefs.filterStatus("favoritesOnly")
        filterSwitchFavorites.setOnCheckedChangeListener {_, isChecked->
            prefs.setShowFavoritesOnly(isChecked)
            refreshMapPins()
        }

        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val task: ServerTaskGetBathrooms = ServerTaskGetBathrooms(this)
        task.start()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        map.isMyLocationEnabled = true
    }

    map.moveCamera(
        CameraUpdateFactory.newLatLngZoom(
            com.google.android.gms.maps.model.LatLng(38.9869, -76.9426),
            15f
        )
    )

    refreshMapPins()
}

    fun refreshMapPins() {

        if (!::map.isInitialized) {
            return
        }

        map.clear()

        var shownBathrooms: List<Bathroom> = bathrooms

        if (prefs.filterStatus("genderNeutralOnly")) {
            shownBathrooms = shownBathrooms.filter { bathroom -> bathroom.isGenderNeutral
            }
        }
        if (prefs.filterStatus("favoritesOnly")) {
            shownBathrooms = shownBathrooms.filter {
                bathroom -> prefs.getFavorites().contains(bathroom.uniqueId)
            }
        }

        val lastViewedBathroomId: String = prefs.getLastViewedBathroomId()

        for (bathroom in shownBathrooms) {
            val markerColor: Float = if (bathroom.uniqueId == lastViewedBathroomId)
                BitmapDescriptorFactory.HUE_ORANGE
            else
                BitmapDescriptorFactory.HUE_RED

            val marker = map.addMarker(
                MarkerOptions()
                    .position(bathroom.latLng)
                    .title(bathroom.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
            )

            marker?.tag = bathroom
        }

        map.setOnMarkerClickListener { marker -> val bathroom: Bathroom = marker.tag as Bathroom

            val intent: Intent = Intent(this, BathroomOverviewActivity::class.java)
            intent.putExtra("bathroomId", bathroom.uniqueId)
            startActivity(intent)

            true
        }
    }

    override fun onResume() {
        super.onResume()
        if (::map.isInitialized) {
            refreshMapPins()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        prefs = LocalPersistentDataHandler(this)
        when (item.itemId) {
            R.id.refresh -> {
                Toast.makeText(this, "Refreshing bathroom data", Toast.LENGTH_SHORT).show()

                val refreshTask: ServerTaskGetBathrooms = ServerTaskGetBathrooms(this)
                refreshTask.start()
            }
        }
        refreshMapPins()
        return true
    }

    fun loadBathrooms(bathrooms: ArrayList<Bathroom>) {
        MainActivity.bathrooms = bathrooms
        refreshMapPins()
    }
}