package com.example.bathroomrater

class Review {
    var uniqueId: String = ""
    var bathroomId: String = ""
    var username: String = ""
    var rating: Double = 0.0
    var comment: String = ""
    var timeOfReview: Long = 0L

    constructor(uniqueId: String, bathroomId: String, username: String,
                rating: Double, comment: String, timeOfReview: Long) {
        this.uniqueId = uniqueId
        this.bathroomId = bathroomId
        this.username = username
        this.rating = rating
        this.comment = comment
        this.timeOfReview = timeOfReview
    }
}