package com.prafullkumar.moviesmate.ui.mainScreen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.prafullkumar.moviesmate.domain.ApiService
import com.prafullkumar.moviesmate.model.Search
import com.prafullkumar.moviesmate.paging.SearchPagingSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class SearchViewModel : ViewModel(), KoinComponent {
    private val apiService by inject<ApiService>()
    var searchResults = MutableStateFlow<PagingData<Search>>(PagingData.empty())

    fun search(searchQuery: String, selectedFilter: String) {
        viewModelScope.launch {
            val pager = Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = {
                    SearchPagingSource(
                        apiService,
                        searchQuery,
                        selectedFilter
                    )
                }
            )
            val flow: Unit =
                pager.flow.cachedIn(viewModelScope).collectLatest { result ->
                    searchResults.update {
                        result
                    }
                }
        }
    }
}