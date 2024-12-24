package com.prafullkumar.moviesmate.domain

import com.prafullkumar.moviesmate.model.Review
import com.prafullkumar.moviesmate.model.UserReview
import com.prafullkumar.moviesmate.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ReviewRepo {
    fun addReview(addReviewClass: UserReview)

    //    fun addReview(review: String, rating: Float, imdbId: String, title: String, poster: String)
    fun hasReviewed(imdbId: String): Flow<Boolean>
    fun deleteUserReview(imdbId: String)
    fun getReviews(imdbId: String): Flow<Resource<List<Review>>>
}
