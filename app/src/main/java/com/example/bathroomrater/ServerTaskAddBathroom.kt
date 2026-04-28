package com.example.bathroomrater

import android.util.Log
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ServerTaskAddBathroom() : Thread() {
    private lateinit var bathroom: Bathroom

    constructor(bathroom: Bathroom) : this() {
        this.bathroom = bathroom
    }

    override fun run() {
        super.run()
        try {
            val url = URL("https://bathroom-rater-20697-default-rtdb.firebaseio.com/bathrooms.json")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")

            val json = """
                {
                    "name": "${bathroom.name}",
                    "building": "${bathroom.building}",
                    "floor": ${bathroom.floor},
                    "latitude": ${bathroom.latLng.latitude},
                    "longitude": ${bathroom.latLng.longitude},
                    "isGenderNeutral": ${bathroom.isGenderNeutral},
                    "isADA": ${bathroom.isADA},
                    "averageRating": ${bathroom.averageRating},
                    "numReviews": ${bathroom.numReviews}
                }
            """.trimIndent()

            val osw = OutputStreamWriter(connection.outputStream)
            osw.write(json)
            osw.flush()
            osw.close()

            val responseCode = connection.responseCode
            Log.d("ServerTaskAddBathroom", "Response code: $responseCode")

            connection.inputStream.close()
        } catch (e: Exception) {
            Log.w("ServerTaskAddBathroom", "exception: " + e.message)
        }
    }
}