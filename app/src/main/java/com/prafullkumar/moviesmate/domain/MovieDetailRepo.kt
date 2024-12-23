package com.prafullkumar.moviesmate.domain

import com.prafullkumar.moviesmate.model.MovieWithReviews
import com.prafullkumar.moviesmate.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MovieDetailRepo {

    suspend fun addToRecentlyViewed(imdbId: String)
    fun getMovieDetails(imdbId: String): Flow<Resource<MovieWithReviews>>
}