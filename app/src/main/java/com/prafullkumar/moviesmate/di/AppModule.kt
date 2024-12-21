package com.prafullkumar.moviesmate.di

import com.prafullkumar.moviesmate.HomeViewModel
import com.prafullkumar.moviesmate.data.AuthenticationRepoImpl
import com.prafullkumar.moviesmate.domain.ApiService
import com.prafullkumar.moviesmate.domain.AuthenticationRepo
import com.prafullkumar.moviesmate.ui.auth.AuthViewModel
import com.prafullkumar.moviesmate.utils.BASE_URL
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val appModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
    single<AuthenticationRepo> { AuthenticationRepoImpl() }
    viewModel { HomeViewModel() }
    viewModel { AuthViewModel() }
}