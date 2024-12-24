package com.prafullkumar.moviesmate.ui.mainScreen.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafullkumar.moviesmate.domain.ApiService
import com.prafullkumar.moviesmate.model.detail.MovieDetail
import com.prafullkumar.moviesmate.utils.API_KEY
import com.prafullkumar.moviesmate.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class UserProfile(
    val username: String,
    val fullName: String,
    val recentlyViewed: List<String>,
    val reviewedShows: List<String>
)

data class UserState(
    val userProfile: UserProfile,
    val recentlyViewedLoading: Boolean,
    val recentlyViewed: List<MovieDetail>? = null,
)

class ProfileRepository : KoinComponent {
    private val auth: FirebaseAuth by inject()
    private val apiService: ApiService by inject()
    private val firestore: FirebaseFirestore by inject()
    private val userName: String = auth.currentUser?.email?.substringBeforeLast("@") ?: ""
    fun getProfileDetails(): Flow<Resource<UserProfile>> {
        return flow {
            try {
                if (userName.isNotBlank()) {
                    val userDetails = firestore.collection("users").document(userName).get().await()
                    if (userDetails != null) {
                        emit(
                            Resource.Success(
                                UserProfile(
                                    username = userDetails.id,
                                    fullName = userDetails["fullName"] as String,
                                    recentlyViewed = userDetails["recentlyViewed"] as List<String>,
                                    reviewedShows = userDetails["reviewedShows"] as List<String>
                                )
                            )
                        )
                    } else {
                        emit(Resource.Error("User not found"))
                    }
                } else {
                    emit(Resource.Error("User not logged in"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An unknown error occurred"))
            }
        }
    }

    fun getRecentlyViewedShows(recentlyViewed: List<String>): Flow<Resource<List<MovieDetail>>> {
        return flow {
            try {
                val movieDetails = mutableListOf<MovieDetail>()
                auth.currentUser!!.email?.let {
                    recentlyViewed.forEach { imdbID ->
                        val movieResponse = apiService.getMovieDetails(
                            apiKey = API_KEY,
                            imdbId = imdbID
                        )
                        if (movieResponse.isSuccessful) {
                            movieDetails.add(movieResponse.body()!!)
                            if (movieDetails.size == recentlyViewed.size) {
                                emit(Resource.Success(movieDetails))
                            }
                        } else {
                            emit(Resource.Error(movieResponse.message()))
                        }
                    }
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An unknown error occurred"))
            }
        }
    }

}

class ProfileVM : ViewModel(), KoinComponent {
    private val context: Context by inject()
    fun changeUsername() {

    }

    fun changePassword() {

    }

    private val profileRepository: ProfileRepository by inject()
    var recentlyViewedLoading by mutableStateOf(false)
    private val _userProfile = MutableStateFlow<Resource<UserState>>(Resource.Loading)
    val userProfile: StateFlow<Resource<UserState>> = _userProfile.asStateFlow()

    init {
        getUserDetails()
    }

    fun getUserDetails() {
        _userProfile.value = Resource.Loading
        viewModelScope.launch {
            profileRepository.getProfileDetails().collect { response ->
                _userProfile.update {
                    when (response) {
                        is Resource.Success -> {
                            Resource.Success(
                                UserState(
                                    userProfile = response.data,
                                    recentlyViewedLoading = false,
                                    recentlyViewed = emptyList()
                                )
                            )
                        }

                        is Resource.Error -> {
                            Resource.Error(response.message)
                        }

                        is Resource.Loading -> {
                            Resource.Loading
                        }

                        Resource.Empty -> {
                            Resource.Empty
                        }
                    }
                }
            }
        }
    }

    fun getRecentlyViewedShows() {
//        if (userProfile.value is Resource.Success && (userProfile.value as Resource.Success<UserState>).data.recentlyViewed != null) {
//            return
//        }
        _userProfile.update {
            Resource.Success(
                UserState(
                    userProfile = (userProfile.value as Resource.Success).data.userProfile,
                    recentlyViewedLoading = true
                )
            )
        }
        viewModelScope.launch {
            try {
                if (userProfile.value is Resource.Success) {
                    val profile = (userProfile.value as Resource.Success).data.userProfile
                    profileRepository.getRecentlyViewedShows(profile.recentlyViewed)
                        .collect { recentlyViewedShows ->
                            Log.d("ProfileVM", "getRecentlyViewedShows: $recentlyViewedShows")
                            if (recentlyViewedShows is Resource.Success) {
                                val userProfile = (userProfile.value as Resource.Success).data
                                _userProfile.update {
                                    Resource.Success(
                                        UserState(
                                            userProfile = userProfile.userProfile,
                                            recentlyViewedLoading = false,
                                            recentlyViewed = recentlyViewedShows.data
                                        )
                                    )
                                }
                            }
                        }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    e.localizedMessage ?: "An unknown error occurred",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                recentlyViewedLoading = false
            }
        }
    }
}