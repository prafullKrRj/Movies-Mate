package com.prafullkumar.moviesmate.model

import com.google.firebase.Timestamp

data class Review(
    val review: String,
    val rating: Double,
    val timestamp: Timestamp,
    val username: String
)
