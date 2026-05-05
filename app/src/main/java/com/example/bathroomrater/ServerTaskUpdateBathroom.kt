package com.example.bathroomrater

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

class ServerTaskUpdateBathroom: Thread {
    private var activity: AddReviewActivity
    private var bathroomId: String = ""
    private var newAvgRating: Double = 0.0
    private var newNumReviews: Int = 0

    constructor(activity: AddReviewActivity, bathroomId: String, newAvgRating: Double, newNumReviews: Int) {
        this.activity = activity
        this.bathroomId = bathroomId
        this.newAvgRating = newAvgRating
        this.newNumReviews = newNumReviews
    }

    override fun run() {
        super.run()
        try {
            val url: URL = URL("https://bathroom-rater-20697-default-rtdb.firebaseio.com/bathrooms/$bathroomId.json")
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH")
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")

            val json: String = """
                {
                    "averageRating": $newAvgRating,
                    "numReviews": $newNumReviews
                }
            """.trimIndent()

            val osw = OutputStreamWriter(connection.outputStream)
            osw.write(json)
            osw.flush()
            osw.close()

            val responseCode = connection.responseCode
            Log.d("UpdateBathroom", "Response code: $responseCode")

            connection.inputStream.close()

            activity.runOnUiThread { activity.onBathroomUpdated() }
        } catch (e: Exception) {
            Log.w("UpdateBathroom", "exception: " + e.message)
        }
    }
}