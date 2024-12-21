package com.prafullkumar.moviesmate.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.moviesmate.domain.AuthenticationRepo
import com.prafullkumar.moviesmate.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthViewModel : ViewModel(), KoinComponent {
    private val authenticationRepo by inject<AuthenticationRepo>()
    private val _loginState = MutableStateFlow<Resource<Boolean>>(Resource.Empty())
    val loginState = _loginState.asStateFlow()
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginState.update { Resource.Loading }
            authenticationRepo.login(email, password).collect { response ->
                _loginState.update {
                    Log.d("Authentication", "loginUser: $response")
                    when (response) {
                        is Resource.Success -> Resource.Success(true)
                        is Resource.Error -> Resource.Error(response.message)
                        else -> Resource.Empty()
                    }
                }
            }
        }
    }

    fun registerUser(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _loginState.update { Resource.Loading }
            Log.d("Authentication", "registerUser: $email $password $fullName")
            val response =
                authenticationRepo.register(email.trim(), password.trim(), fullName).collect { response ->
                    _loginState.update {
                        when (response) {
                            is Resource.Success -> Resource.Success(true)
                            is Resource.Error -> Resource.Error(response.message)
                            else -> Resource.Empty()
                        }
                    }
                }
        }
    }

    fun changeUsername(username: String) {
        viewModelScope.launch {
            authenticationRepo.changeUsername(username)
        }
    }
}