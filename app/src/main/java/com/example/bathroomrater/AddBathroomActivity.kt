package com.example.bathroomrater

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import java.util.UUID

class AddBathroomActivity : AppCompatActivity() {
    private lateinit var bathroomName: EditText
    private lateinit var  bathroomBuilding: EditText
    private lateinit var  bathroomFloor: EditText
    private lateinit var  bathroomLatitude: EditText
    private lateinit var  bathroomLongitude: EditText
    private lateinit var  isGenderNeutral: SwitchCompat
    private lateinit var  isAda: SwitchCompat
    private lateinit var  addBathroomButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bathroom)
        supportActionBar?.hide()

        bathroomName = findViewById<EditText>(R.id.name)
        bathroomBuilding = findViewById<EditText>(R.id.building)
        bathroomFloor = findViewById<EditText>(R.id.floor)
        bathroomLatitude = findViewById<EditText>(R.id.latitude)
        bathroomLongitude = findViewById<EditText>(R.id.longitude)
        isGenderNeutral = findViewById<SwitchCompat>(R.id.is_gender_neutral)
        isAda = findViewById<SwitchCompat>(R.id.is_ada)
        addBathroomButton = findViewById<Button>(R.id.add_bathroom)

        addBathroomButton.setOnClickListener {
            val nameStr: String = bathroomName.text.toString().trim()
            val buildingStr: String = bathroomBuilding.text.toString().trim()
            val floorStr: String = bathroomFloor.text.toString().trim()
            val latitudeStr: String = bathroomLatitude.text.toString().trim()
            val longitudeStr: String = bathroomLongitude.text.toString().trim()

            if (nameStr.isEmpty() || buildingStr.isEmpty() || floorStr.isEmpty() ||
                latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val floor: Int = floorStr.toInt()
                val latitude: Double = latitudeStr.toDouble()
                val longitude: Double = longitudeStr.toDouble()
                val uniqueId: String = UUID.randomUUID().toString()

                val newBathroom = Bathroom(
                    uniqueId,
                    nameStr,
                    buildingStr,
                    floor,
                    latitude,
                    longitude,
                    isGenderNeutral.isChecked,
                    isAda.isChecked,
                    0.0,
                    0
                )

                val task: ServerTaskAddBathroom = ServerTaskAddBathroom(newBathroom)
                task.start()

                Toast.makeText(this, "Bathroom Added!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
            }
        }

        backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener {
            this.finish()
        }
    }
}