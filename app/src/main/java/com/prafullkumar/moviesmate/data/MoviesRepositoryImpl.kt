package com.prafullkumar.moviesmate.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.prafullkumar.moviesmate.domain.ApiService
import com.prafullkumar.moviesmate.domain.MoviesRepo
import com.prafullkumar.moviesmate.model.movies.Search
import com.prafullkumar.moviesmate.paging.MoviePagingSource
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MoviesRepositoryImpl : MoviesRepo, KoinComponent {
    private val apiService: ApiService by inject()
    override fun getMoviesStream(
        apiKey: String,
        type: String,
        search: String,
        year: Int?
    ): Flow<PagingData<Search>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = 1
            ),
            pagingSourceFactory = { MoviePagingSource(apiService, apiKey, type, search, year) }
        ).flow
    }

}