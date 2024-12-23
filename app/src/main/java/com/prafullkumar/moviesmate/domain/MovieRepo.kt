package com.prafullkumar.moviesmate.domain

import androidx.paging.PagingData
import com.prafullkumar.moviesmate.model.Search
import kotlinx.coroutines.flow.Flow

interface MovieRepo {
    fun getMoviesStream(
        apiKey: String,
        type: String,
        search: String,
        year: Int?
    ): Flow<PagingData<Search>>

}