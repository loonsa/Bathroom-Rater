package com.example.bathroomrater

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.URL
import java.util.Scanner

class ServerTaskGetBathrooms: Thread {
    private var mainActivity: MainActivity

    constructor(mainActivity: MainActivity) {
        this.mainActivity = mainActivity
    }

    override fun run() {
        super.run()
        var result: String = ""
        try {
            val url: URL = URL("https://bathroom-rater-20697-default-rtdb.firebaseio.com/bathrooms.json")
            val iStream: InputStream = url.openStream()
            val scan: Scanner = Scanner( iStream )
            while (scan.hasNext()) {
                result += scan.nextLine()
            }

            val bathrooms: ArrayList<Bathroom> = parseJSON(result)
            mainActivity.runOnUiThread{ mainActivity.loadBathrooms(bathrooms) } //loadBathrooms needs to be implemented
        } catch(e: Exception ) {
            Log.w( "MainActivity", "exception: " + e.message )
        }
    }

    fun parseJSON(json: String): ArrayList<Bathroom> {
        val bathrooms: ArrayList<Bathroom> = arrayListOf()
        try {
            val jsonObject: JSONObject = JSONObject(json)
            val keys: Iterator<String> = jsonObject.keys()
            while (keys.hasNext()) {
                val uniqueId: String = keys.next()
                val bathroomData: JSONObject = jsonObject.getJSONObject(uniqueId)
                val bathroom: Bathroom = Bathroom(
                    uniqueId,
                    bathroomData.getString("name"),
                    bathroomData.getString("building"),
                    bathroomData.getInt("floor"),
                    bathroomData.getDouble("latitude"),
                    bathroomData.getDouble("longitude"),
                    bathroomData.getBoolean("isGenderNeutral"),
                    bathroomData.getBoolean("isADA"),
                    bathroomData.getDouble("averageRating"),
                    bathroomData.getInt("numReviews")
                )
                bathrooms.add(bathroom)
            }
        } catch (e: Exception) {
            Log.w( "MainActivity", "exception: " + e.message )
        }

        return bathrooms
    }
}