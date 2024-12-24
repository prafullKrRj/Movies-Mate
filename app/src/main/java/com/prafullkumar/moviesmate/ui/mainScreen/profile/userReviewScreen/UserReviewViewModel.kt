package com.prafullkumar.moviesmate.ui.mainScreen.profile.userReviewScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafullkumar.moviesmate.model.UserReview
import com.prafullkumar.moviesmate.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class UserReviewViewModel : ViewModel(), KoinComponent {
    val username: String =
        FirebaseAuth.getInstance().currentUser?.email?.substringBeforeLast("@") ?: ""
    private val userReviewRepository: UserReviewRepository by inject()
    val userReviews: StateFlow<Resource<List<UserReview>>> = userReviewRepository.getUserReviews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Resource.Loading)
}


class UserReviewRepository : KoinComponent {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val username: String = firebaseAuth.currentUser?.email?.substringBeforeLast("@") ?: ""

    fun getUserReviews(): Flow<Resource<List<UserReview>>> {
        return flow {
            try {
                if (username.isNotBlank()) {
                    val reviewList =
                        firestore.collection("users").document(username).collection("reviews").get()
                            .await()
                    val reviews = mutableListOf<UserReview>()
                    for (review in reviewList) {
                        reviews.add(
                            UserReview(
                                imdbId = review.getString("imdbId") ?: "",
                                showName = review.getString("showName") ?: "",
                                poster = review.getString("poster") ?: "",
                                review = review.getString("review") ?: "",
                                rating = review.getDouble("rating") ?: 0.0,
                                timestamp = review.getTimestamp("timestamp") ?: Timestamp.now()
                            )
                        )
                    }
                    emit(Resource.Success(reviews))
                } else {
                    emit(Resource.Error("User not found"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An unknown error occurred"))
            }
        }
    }
}