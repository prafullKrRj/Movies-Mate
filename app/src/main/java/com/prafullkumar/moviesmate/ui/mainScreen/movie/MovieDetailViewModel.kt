package com.prafullkumar.moviesmate.ui.mainScreen.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.moviesmate.MainAppRoutes
import com.prafullkumar.moviesmate.domain.MovieDetailRepo
import com.prafullkumar.moviesmate.model.MovieWithReviews
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
    private val movieDetailRepo: MovieDetailRepo by inject()


    private val _movieDetail = MutableStateFlow<Resource<MovieWithReviews>>(Resource.Loading)
    val movieDetail = _movieDetail.asStateFlow()

    init {
        getMovieDetails()
    }

    fun getMovieDetails() {
        _movieDetail.update { Resource.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                movieDetailRepo.getMovieDetails(movie.id).collect { response ->
                    _movieDetail.update { response }
                }
            } catch (e: Exception) {
                _movieDetail.value = Resource.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }

    private fun addToRecentlyViewed() = viewModelScope.launch {
        movieDetailRepo.addToRecentlyViewed(movie.id)
    }
}