package com.example.bathroomrater

import android.util.Log
import org.json.JSONObject
import java.io.InputStream
import java.net.URL
import java.util.Scanner

class ServerTaskGetReviews : Thread {
    private var activity: BathroomOverviewActivity
    private var bathroomId: String

    constructor(activity: BathroomOverviewActivity, bathroomId: String) {
        this.activity = activity
        this.bathroomId = bathroomId
    }

    override fun run() {
        super.run()
        var result = ""
        try {
            val url = URL("https://bathroom-rater-20697-default-rtdb.firebaseio.com/reviews.json")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            val iStream: InputStream = connection.inputStream
            val scan = Scanner(iStream)
            while (scan.hasNext()) {
                result += scan.nextLine()
            }

            val reviews = parseJSON(result)
            activity.runOnUiThread { activity.loadReviews(reviews) }
        } catch (e: Exception) {
            Log.w("ServerTaskGetReviews", "exception: " + e.message)
        }
    }

    fun parseJSON(json: String): ArrayList<Review> {
        val reviews = arrayListOf<Review>()
        try {
            if (json == "null") return reviews
            val jsonObject = JSONObject(json)
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val uniqueId = keys.next()
                val data = jsonObject.getJSONObject(uniqueId)
                if (data.getString("bathroomId") == bathroomId) {
                    val review = Review(
                        uniqueId,
                        data.getString("bathroomId"),
                        data.getString("username"),
                        data.getDouble("rating"),
                        data.getString("comment"),
                        data.getLong("timeOfReview")
                    )
                    reviews.add(review)
                }
            }
        } catch (e: Exception) {
            Log.w("ServerTaskGetReviews", "parse exception: " + e.message)
        }
        return reviews
    }
}