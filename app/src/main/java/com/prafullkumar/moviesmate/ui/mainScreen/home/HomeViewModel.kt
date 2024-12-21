package com.prafullkumar.moviesmate.ui.mainScreen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafullkumar.moviesmate.domain.ApiService
import com.prafullkumar.moviesmate.model.Movies
import com.prafullkumar.moviesmate.utils.API_KEY
import com.prafullkumar.moviesmate.utils.Resource
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
    private val _popularInYourRegion = MutableStateFlow<Resource<Movies>>(Resource.Loading)

    private val _recentlyViewed = MutableStateFlow<Resource<List<String>>>(Resource.Loading)
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

    fun getLatestMovies() {
        viewModelScope.launch {
            _latestMovies.value = Resource.Loading
            try {
                val response = apiService.getGenericMovies(
                    apiKey = API_KEY,
                    year = Calendar.getInstance().get(Calendar.YEAR),
                    type = "movie",
                    search = "movie"
                )
                if (response.isSuccessful) {
                    _latestMovies.update { Resource.Success(response.body()!!) }
                } else {
                    _latestMovies.value = Resource.Error(response.message())
                }
            } catch (e: Exception) {
                _latestMovies.value = e.localizedMessage?.let { Resource.Error(it) }!!
            }
        }
    }

    fun getLatestSeries() {
        viewModelScope.launch {
            _latestSeries.value = Resource.Loading
            try {
                val response = apiService.getGenericMovies(
                    apiKey = API_KEY,
                    year = Calendar.getInstance().get(Calendar.YEAR),
                    type = "series",
                    search = "series"
                )
                if (response.isSuccessful) {
                    _latestSeries.update { Resource.Success(response.body()!!) }
                } else {
                    _latestSeries.value = Resource.Error(response.message())
                }
            } catch (e: Exception) {
                _latestSeries.value = e.localizedMessage?.let { Resource.Error(it) }!!
            }
        }
    }

    fun getPopularInYourRegion() {
        viewModelScope.launch {
            _popularInYourRegion.value = Resource.Loading
//            try {
//                val response = apiService.getPopularInYourRegion()
//                _popularInYourRegion.value = Resource.Success(response)
//            } catch (e: Exception) {
//                _popularInYourRegion.value = Resource.Error(e.localizedMessage)
//            }
        }
    }

    fun getRecentlyViewed() {
        viewModelScope.launch {
            _recentlyViewed.value = Resource.Loading
            try {
                val response = auth.currentUser!!.email?.let { it ->
                    firestore.collection("users")
                        .document(it.substringBeforeLast("@")).get().addOnSuccessListener {documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                val recentlyViewed = documentSnapshot["recentlyViewed"] as List<String>
                                Log.d("HomeViewModel", "getRecentlyViewed: $recentlyViewed")
                                _recentlyViewed.update { Resource.Success(recentlyViewed) }
                            } else {
                                _recentlyViewed.value = Resource.Empty()
                            }
                        }
                }
            } catch (e: Exception) {
                _recentlyViewed.value = e.localizedMessage?.let { Resource.Error(it) }!!
            }
        }
    }
}