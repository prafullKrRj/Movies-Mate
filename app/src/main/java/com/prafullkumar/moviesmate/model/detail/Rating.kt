package com.prafullkumar.moviesmate.model.detail

import com.google.gson.annotations.SerializedName

data class Rating(
    @SerializedName("Source")
    val Source: String,
    @SerializedName("Value")
    val Value: String
)