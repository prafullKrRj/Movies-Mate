package com.prafullkumar.moviesmate.ui.mainScreen.movie.reviewScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.prafullkumar.moviesmate.MainAppRoutes
import com.prafullkumar.moviesmate.domain.ReviewRepo
import com.prafullkumar.moviesmate.model.Review
import com.prafullkumar.moviesmate.model.UserReview
import com.prafullkumar.moviesmate.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReviewViewModel(
    val movie: MainAppRoutes.ReviewScreen
) : ViewModel(), KoinComponent {

    private val reviewRepo: ReviewRepo by inject()
    private val _reviews = MutableStateFlow<Resource<List<Review>>>(Resource.Loading)
    val reviews = _reviews.asStateFlow()
    var hasReviewed by mutableStateOf(false)

    init {
        getReviews()
    }

    fun getReviews() {
        viewModelScope.launch {
            reviewRepo.getReviews(movie.id).collect { response ->
                _reviews.update { response }
            }
        }
    }

    fun addReview(review: String, rating: Double) {
        viewModelScope.launch {
            reviewRepo.addReview(
                UserReview(
                    showName = movie.title,
                    poster = movie.poster,
                    review = review,
                    rating = rating.toDouble(),
                    timestamp = Timestamp.now(),
                    imdbId = movie.id
                )
            )
        }
    }

    fun deleteYourReview() {
        viewModelScope.launch {
            _reviews.update { Resource.Success(emptyList()) }
        }
    }

    fun hasReviewed() {
        viewModelScope.launch {
            reviewRepo.hasReviewed(movie.id).collect {
                hasReviewed = it
            }
        }
    }
}