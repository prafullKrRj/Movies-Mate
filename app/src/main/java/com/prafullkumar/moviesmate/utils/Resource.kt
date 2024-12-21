package com.prafullkumar.moviesmate.utils

sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val message: String) : Resource<Nothing>
    data object Loading : Resource<Nothing>
    data class Empty(val message: String = "Empty") : Resource<Nothing>
}