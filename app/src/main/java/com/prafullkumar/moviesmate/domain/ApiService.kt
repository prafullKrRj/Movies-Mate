package com.prafullkumar.moviesmate.domain

import com.prafullkumar.moviesmate.model.Movies
import com.prafullkumar.moviesmate.model.detail.MovieDetail
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("?")
    suspend fun getMovieList(
        @Query("apikey") apiKey: String,
        @Query("s") search: String,
        @Query("type") type: String,
        @Query("page") page: Int? = null
    ): Response<Movies>


    @GET("?")
    suspend fun getGenericMovies(
        @Query("apikey") apiKey: String,
        @Query("s") search: String,
        @Query("type") type: String,
        @Query("y") year: Int? = null,
        @Query("page") page: Int? = null
    ): Response<Movies>

    @GET("?")
    suspend fun getMovieDetails(
        @Query("apikey") apiKey: String,
        @Query("i") imdbId: String,
    ): Response<MovieDetail>
}