package com.prafullkumar.moviesmate.model.detail

import com.google.gson.annotations.SerializedName

data class MovieDetail(
    @SerializedName("Actors")
    val Actors: String?,
    @SerializedName("Awards")
    val Awards: String?,
    @SerializedName("BoxOffice")
    val BoxOffice: String?,
    @SerializedName("Country")
    val Country: String?,
    @SerializedName("DVD")
    val DVD: String?,
    @SerializedName("Director")
    val Director: String?,
    @SerializedName("Genre")
    val Genre: String?,
    @SerializedName("Language")
    val Language: String?,
    @SerializedName("Metascore")
    val Metascore: String?,
    @SerializedName("Plot")
    val Plot: String?,
    @SerializedName("Poster")
    val Poster: String?,
    @SerializedName("Production")
    val Production: String?,
    @SerializedName("Rated")
    val Rated: String?,
    @SerializedName("Ratings")
    val Ratings: List<Rating>?,
    @SerializedName("Released")
    val Released: String?,
    @SerializedName("Response")
    val Response: String?,
    @SerializedName("Runtime")
    val Runtime: String?,
    @SerializedName("Title")
    val Title: String?,
    @SerializedName("Type")
    val Type: String?,
    @SerializedName("Website")
    val Website: String?,
    @SerializedName("Writer")
    val Writer: String?,
    @SerializedName("Year")
    val Year: String?,
    @SerializedName("imdbID")
    val imdbID: String?,
    @SerializedName("imdbRating")
    val imdbRating: String?,
    @SerializedName("imdbVotes")
    val imdbVotes: String?
)