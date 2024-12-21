package com.prafullkumar.moviesmate.domain

import com.prafullkumar.moviesmate.model.Movies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("?")
    suspend fun getMovieList(
        @Query("apikey") apiKey: String,
        @Query("s") search: String,
        @Query("type") type: String
    ): Response<Movies>
}