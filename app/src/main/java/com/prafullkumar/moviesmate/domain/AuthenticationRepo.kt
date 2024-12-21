package com.prafullkumar.moviesmate.domain

import com.prafullkumar.moviesmate.data.User
import com.prafullkumar.moviesmate.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepo {

    fun login(username: String, password: String): Flow<Resource<User>>
    fun register(username: String, password: String, fullName: String): Flow<Resource<User>>
    fun changeUsername(userName: String): Flow<Resource<User>>
}
