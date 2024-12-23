package com.prafullkumar.moviesmate.ui.mainScreen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafullkumar.moviesmate.domain.ApiService
import com.prafullkumar.moviesmate.model.Movies
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
import java.util.Calendar

class HomeViewModel : ViewModel(), KoinComponent {
    private val apiService by inject<ApiService>()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _latestMovies = MutableStateFlow<Resource<Movies>>(Resource.Loading)
    val latestMovies = _latestMovies.asStateFlow()
    private val _latestSeries = MutableStateFlow<Resource<Movies>>(Resource.Loading)
    val latestSeries = _latestSeries.asStateFlow()

    private val _recentlyViewed = MutableStateFlow<Resource<List<MovieDetail>>>(Resource.Loading)
    val recentlyViewed = _recentlyViewed.asStateFlow()

    init {
        if (_latestMovies.value !is Resource.Success) {
            getLatestMovies()
        }
        if (_latestSeries.value !is Resource.Success) {
            getLatestSeries()
        }
        if (_recentlyViewed.value !is Resource.Success) {
            getRecentlyViewed()
        }
    }

    fun getLatestMovies() =
        getShow("movie", "movie", Calendar.getInstance().get(Calendar.YEAR), _latestMovies)

    fun getLatestSeries() {
        getShow("series", "series", Calendar.getInstance().get(Calendar.YEAR), _latestSeries)
    }

    private fun getShow(
        search: String,
        type: String,
        year: Int,
        flow: MutableStateFlow<Resource<Movies>>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            flow.value = Resource.Loading
            try {
                val response = apiService.getGenericMovies(
                    apiKey = API_KEY,
                    year = year,
                    type = type,
                    search = search
                )
                if (response.isSuccessful) {
                    flow.update { Resource.Success(response.body()!!) }
                } else {
                    flow.value = Resource.Error(response.message())
                }
            } catch (e: Exception) {
                flow.value = e.localizedMessage?.let { Resource.Error(it) }!!
            }
        }
    }

    fun getRecentlyViewed() {
        viewModelScope.launch {
            _recentlyViewed.value = Resource.Loading
            try {

                val movieDetails = mutableListOf<MovieDetail>()
                auth.currentUser!!.email?.let { it ->
                    firestore.collection("users")
                        .document(it.substringBeforeLast("@")).get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                val recentlyViewed =
                                    documentSnapshot["recentlyViewed"] as List<String>
                                recentlyViewed.forEach { imdbID ->
                                    viewModelScope.launch {
                                        val movieResponse = apiService.getMovieDetails(
                                            apiKey = API_KEY,
                                            imdbId = imdbID
                                        )
                                        if (movieResponse.isSuccessful) {
                                            movieDetails.add(movieResponse.body()!!)
                                            if (movieDetails.size == recentlyViewed.size) {
                                                _recentlyViewed.update {
                                                    Resource.Success(
                                                        movieDetails
                                                    )
                                                }
                                            }
                                        } else {
                                            _recentlyViewed.value =
                                                Resource.Error(movieResponse.message())
                                        }
                                    }
                                }
                            } else {
                                _recentlyViewed.value = Resource.Empty
                            }
                        }
                    Log.d("HomeViewModel", movieDetails.toString())
                }
//                _recentlyViewed.update { Resource.Success(movieDetails) }
            } catch (e: Exception) {
                _recentlyViewed.value = e.localizedMessage?.let { Resource.Error(it) }!!
            }
        }
    }
}