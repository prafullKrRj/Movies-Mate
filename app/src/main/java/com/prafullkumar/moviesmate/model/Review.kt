package com.prafullkumar.moviesmate.model

import com.google.firebase.Timestamp

data class Review(
    val review: String,
    val rating: Double,
    val timestamp: Timestamp,
    val username: String
)

data class UserReview(
    val review: String,
    val poster: String,
    val showName: String,
    val rating: Double,
    val timestamp: Timestamp,
    val imdbId: String
) {
    fun toReview(username: String = ""): Review {
        return Review(review, rating, timestamp, username)
    }
}