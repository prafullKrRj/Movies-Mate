package com.prafullkumar.moviesmate.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafullkumar.moviesmate.domain.ReviewRepo
import com.prafullkumar.moviesmate.model.Review
import com.prafullkumar.moviesmate.model.UserReview
import com.prafullkumar.moviesmate.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class ReviewRepoImpl : ReviewRepo, KoinComponent {
    private val firestore by inject<FirebaseFirestore>()
    private val auth by inject<FirebaseAuth>()
    private val username: String = auth.currentUser?.email?.substringBeforeLast("@") ?: ""
    override fun addReview(review: UserReview) {
        try {
            if (username.isNotBlank()) {
                firestore.collection("users").document(username).get().addOnSuccessListener {
                    val reviewedShows = it["reviewedShows"] as List<String>
                    val updatedReviewedShows = mutableListOf<String>()
                    updatedReviewedShows.addAll(reviewedShows)
                    updatedReviewedShows.add(review.imdbId)
                    firestore.collection("users").document(username)
                        .update("reviewedShows", updatedReviewedShows)
                    firestore.collection("users").document(username)
                        .collection("reviews").document(review.imdbId).set(
                            review // UserReview to map
                        )
                }
                firestore.collection("movies").document(review.imdbId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val totalReviews = document["totalReviews"] as? Long ?: 0L
                            val totalRates = document["totalRates"] as? Long ?: 0L
                            val avgRating = document["avgRating"] as? Double ?: 0.0
                            val updatedTotalReviews = totalReviews + 1
                            val updatedTotalRates = totalRates + review.rating
                            val updatedAvgRating =
                                (avgRating * totalRates + review.rating) / updatedTotalRates
                            firestore.collection("movies").document(review.imdbId).update(
                                mapOf(
                                    "totalReviews" to updatedTotalReviews,
                                    "movieName" to review.showName,
                                    "totalRates" to updatedTotalRates,
                                    "avgRating" to updatedAvgRating
                                )
                            )
                        } else {
                            firestore.collection("movies").document(review.imdbId).set(
                                mapOf(
                                    "totalReviews" to 1L,
                                    "totalRates" to 1,
                                    "movieName" to review.showName,
                                    "avgRating" to review.rating
                                )
                            )
                        }
                        firestore.collection("movies").document(review.imdbId).collection("reviews")
                            .add(
                                mapOf(
                                    "review" to review.review,
                                    "rating" to review.rating,
                                    "timestamp" to Timestamp.now(),
                                    "username" to username
                                )
                            )
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun hasReviewed(imdbId: String): Flow<Boolean> = flow {
        try {
            if (username.isNotBlank()) {
                val document = firestore.collection("users").document(username).get().await()
                val reviews = document["reviewedShows"] as List<String>
                emit(reviews.contains(imdbId))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun deleteUserReview(imdbId: String) {
        try {

        } catch (e: Exception) {

        }
    }


    override fun getReviews(imdbId: String): Flow<Resource<List<Review>>> = flow {
        try {
            val reviews =
                firestore.collection("movies").document(imdbId).collection("reviews").get().await()
            val reviewList = mutableListOf<Review>()
            for (review in reviews.documents) {
                reviewList.add(
                    Review(
                        review = review["review"] as String,
                        rating = review["rating"] as Double,
                        timestamp = review["timestamp"] as Timestamp,
                        username = review["username"] as String
                    )
                )
            }
            Log.d("ReviewRepoImpl", "getReviews: $reviewList")
            emit(Resource.Success(reviewList))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
        }
    }
}