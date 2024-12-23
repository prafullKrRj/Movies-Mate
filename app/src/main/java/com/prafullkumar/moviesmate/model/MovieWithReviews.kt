package com.prafullkumar.moviesmate.model

import com.prafullkumar.moviesmate.model.detail.MovieDetail

data class MovieWithReviews(
    val movie: MovieDetail,
//    val reviews: List<Review>,
    val avgRating: Double,
    val totalRates: Long,
    val totalReviews: Long
)
