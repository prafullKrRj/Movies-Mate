package com.prafullkumar.moviesmate.ui.mainScreen.movie

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafullkumar.moviesmate.MainAppRoutes
import com.prafullkumar.moviesmate.domain.ApiService
import com.prafullkumar.moviesmate.model.detail.MovieDetail
import com.prafullkumar.moviesmate.utils.API_KEY
import com.prafullkumar.moviesmate.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MovieDetailViewModel(
    val movie: MainAppRoutes.MovieDetailScreen
) : ViewModel(), KoinComponent {
    private val apiService: ApiService by inject()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _movieDetail = MutableStateFlow<Resource<MovieDetail>>(Resource.Loading)
    val movieDetail = _movieDetail.asStateFlow()

    init {
        getMovieDetails()
    }

    fun getMovieDetails() {
        _movieDetail.update { Resource.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getMovieDetails(apiKey = API_KEY, movie.id)
                if (response.isSuccessful) {
                    _movieDetail.update { Resource.Success(response.body()!!) }
                    addToRecentlyViewed()
                } else {
                    _movieDetail.update { Resource.Error(response.message()) }
                }
            } catch (e: Exception) {
                _movieDetail.value = Resource.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }

    private fun addToRecentlyViewed() {
        viewModelScope.launch {
            Log.d("MovieDetailViewModel", "addToRecentlyViewed: ${movie.id}")
            Log.d("MovieDetailViewModel", "addToRecentlyViewed: ${auth.currentUser?.email}")
            auth.currentUser!!.email?.substringBeforeLast("@")?.let {
                firestore.collection("users")
                    .document(it).get()
                    .addOnSuccessListener { document ->
                        Log.d("MovieDetailViewModel", "addToRecentlyViewed: ${document.data}")
                        val recentlyViewed = document["recentlyViewed"] as List<String>
                        if (recentlyViewed.contains(movie.id)) {
                            return@addOnSuccessListener
                        }
                        val updatedRecentlyViewed = mutableListOf<String>()
                        updatedRecentlyViewed.addAll(recentlyViewed)
                        updatedRecentlyViewed.add(movie.id)
                        firestore.collection("users")
                            .document(it)
                            .update("recentlyViewed", updatedRecentlyViewed)
                    }
            }
        }
    }
}