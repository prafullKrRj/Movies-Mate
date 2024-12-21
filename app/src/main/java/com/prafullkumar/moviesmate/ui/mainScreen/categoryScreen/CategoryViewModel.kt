package com.prafullkumar.moviesmate.ui.mainScreen.categoryScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.moviesmate.MainAppRoutes
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
import java.util.Locale

enum class Type {
    MOVIE, SERIES, GENRE
}

class CategoryViewModel(
    val category: MainAppRoutes.CategoryScreen
) : ViewModel(), KoinComponent {

    private val apiService: ApiService by inject()
    private val _shows = MutableStateFlow<Resource<Movies>>(Resource.Loading)
    val shows = _shows.asStateFlow()

    init {
        getShows()
    }

    fun getShows() {
        viewModelScope.launch {
            try {
                val response = apiService.getGenericMovies(
                    apiKey = API_KEY,
                    type = if (category.type == Type.GENRE) "movie" else category.type.name.toLowerCase(
                        Locale.ROOT
                    ),
                    search = category.category
                )
                if (response.isSuccessful) {
                    _shows.update {
                        Resource.Success(response.body()!!)
                    }
                } else {
                    _shows.update {
                        Resource.Error(response.message())
                    }
                }
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "getShows: ${e.message}")
                _shows.value = Resource.Error("An error occurred")
            }
        }
    }
}