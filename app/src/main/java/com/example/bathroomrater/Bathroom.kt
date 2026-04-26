package com.example.bathroomrater

import com.google.android.gms.maps.model.LatLng

class Bathroom {
    var uniqueId: String = ""
    var name: String = ""
    var building: String = ""
    var floor: Int = 0
    var latLng: LatLng
    var isGenderNeutral: Boolean = false
    var isADA: Boolean = false
    var averageRating: Double = 0.0
    var numReviews: Int = 0

    constructor(uniqueId: String, name: String, building: String, floor: Int,
                latitude: Double, longitude: Double, isGenderNeutral: Boolean,
                isADA: Boolean, averageRating: Double, numReviews: Int) {
        this.uniqueId = uniqueId
        this.name = name
        this.building = building
        this.floor = floor
        this.latLng = LatLng(latitude, longitude)
        this.isGenderNeutral = isGenderNeutral
        this.isADA = isADA
        this.averageRating = averageRating
        this.numReviews = numReviews
    }
}