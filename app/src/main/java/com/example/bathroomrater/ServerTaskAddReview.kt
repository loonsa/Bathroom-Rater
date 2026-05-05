package com.example.bathroomrater

import android.util.Log
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ServerTaskAddReview() : Thread() {
    private lateinit var activity: AddReviewActivity
    private lateinit var review: Review

    constructor(activity: AddReviewActivity, review: Review) : this() {
        this.activity = activity
        this.review = review
    }

    override fun run() {
        super.run()
        try {
            val url = URL("https://bathroom-rater-20697-default-rtdb.firebaseio.com/reviews.json")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")

            val json = """
            {
                "bathroomId": "${review.bathroomId}",
                "username": "${review.username}",
                "rating": ${review.rating},
                "comment": "${review.comment}",
                "timeOfReview": ${review.timeOfReview}
            }
        """.trimIndent()

            val osw = OutputStreamWriter(connection.outputStream)
            osw.write(json)
            osw.flush()
            osw.close()

            val responseCode = connection.responseCode
            Log.d("ServerTaskAddReview", "Response code: $responseCode")

            connection.inputStream.close()

            activity.runOnUiThread { activity.onReviewAdded() }
        } catch (e: Exception) {
            Log.w("ServerTaskAddReview", "exception: " + e.message)
        }
    }
}