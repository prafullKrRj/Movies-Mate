package com.prafullkumar.moviesmate.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafullkumar.moviesmate.domain.ApiService
import com.prafullkumar.moviesmate.domain.MovieDetailRepo
import com.prafullkumar.moviesmate.model.MovieWithReviews
import com.prafullkumar.moviesmate.utils.API_KEY
import com.prafullkumar.moviesmate.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MovieDetailRepoImpl : MovieDetailRepo, KoinComponent {
    private val firestore by inject<FirebaseFirestore>()
    private val auth by inject<FirebaseAuth>()
    private val apiService by inject<ApiService>()
    override suspend fun addToRecentlyViewed(imdbId: String) {
        try {
            auth.currentUser!!.email?.substringBeforeLast("@")?.let {
                val document = firestore.collection("users")
                    .document(it).get().await()
                val recentlyViewed = document["recentlyViewed"] as List<String>
                if (recentlyViewed.contains(imdbId)) {
                    return
                }
                val updatedRecentlyViewed = mutableListOf<String>()
                updatedRecentlyViewed.addAll(recentlyViewed)
                updatedRecentlyViewed.add(imdbId)
                firestore.collection("users")
                    .document(it)
                    .update("recentlyViewed", updatedRecentlyViewed)
            }
        } catch (e: Exception) {
            Log.d("MovieDetailRepo", "Error in adding to recentlyViewed")
        }
    }

    override fun getMovieDetails(imdbId: String): Flow<Resource<MovieWithReviews>> = flow {
        emit(Resource.Loading)
        try {
            var avgRating = 0.0
            var totalRates: Long = 0
            var totalReviews: Long = 0
            val document = firestore.collection("movies").document(imdbId).get().await()
            if (document.exists()) {
                document.get("avgRating")?.let { avgRating = it as Double }
                document.get("totalRates")?.let { totalRates = it as Long }
                document.get("totalReviews")?.let { totalReviews = it as Long }
            }
            val response = apiService.getMovieDetails(API_KEY, imdbId)
            if (response.isSuccessful) {
                response.body()?.let { movieDetail ->
                    Log.d(
                        "MovieDetailRepo",
                        "getMovieDetails: $avgRating $totalRates $totalReviews"
                    )
                    emit(
                        Resource.Success(
                            MovieWithReviews(
                                movieDetail,
                                avgRating,
                                totalRates,
                                totalReviews
                            )
                        )
                    )
                }
            } else {
                emit(Resource.Error("Error in fetching movie details"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
        }
    }
}