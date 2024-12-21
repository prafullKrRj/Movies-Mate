package com.prafullkumar.moviesmate.ui.mainScreen.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.moviesmate.domain.ApiService
import com.prafullkumar.moviesmate.model.Movies
import com.prafullkumar.moviesmate.utils.API_KEY
import com.prafullkumar.moviesmate.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchViewModel : ViewModel(), KoinComponent {
    private val apiService by inject<ApiService>()
    private val _searchResults = MutableStateFlow<Resource<Movies>>(Resource.Empty())
    val searchResults = _searchResults.asStateFlow()
    fun search(searchQuery: String, selectedFilter: String) {
        _searchResults.update { Resource.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getMovieList(
                    apiKey = API_KEY,
                    search = searchQuery,
                    type = if (selectedFilter.lowercase() == "all") "" else selectedFilter
                )
                if (response.isSuccessful) {
                    Log.d("SearchViewModel", "search: ${response.body()}")
                    response.body()?.let { movies ->
                        if (movies.totalResults == null) {
                            _searchResults.update {
                                Resource.Error("Too many results or no result")
                            }
                        } else {
                            _searchResults.update { Resource.Success(movies) }
                        }
                    }
                } else {
                    _searchResults.update { Resource.Error(response.message()) }
                }
            } catch (e: Exception) {
                _searchResults.update { Resource.Error(e.message.toString()) }
            }
        }
    }
}