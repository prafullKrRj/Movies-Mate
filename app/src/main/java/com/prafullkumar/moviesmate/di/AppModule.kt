package com.prafullkumar.moviesmate.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafullkumar.moviesmate.data.AuthenticationRepoImpl
import com.prafullkumar.moviesmate.data.MovieDetailRepoImpl
import com.prafullkumar.moviesmate.data.MoviesRepositoryImpl
import com.prafullkumar.moviesmate.data.ReviewRepoImpl
import com.prafullkumar.moviesmate.domain.ApiService
import com.prafullkumar.moviesmate.domain.AuthenticationRepo
import com.prafullkumar.moviesmate.domain.MovieDetailRepo
import com.prafullkumar.moviesmate.domain.MoviesRepo
import com.prafullkumar.moviesmate.domain.ReviewRepo
import com.prafullkumar.moviesmate.ui.auth.AuthViewModel
import com.prafullkumar.moviesmate.ui.mainScreen.categoryScreen.MovieListViewModel
import com.prafullkumar.moviesmate.ui.mainScreen.home.HomeViewModel
import com.prafullkumar.moviesmate.ui.mainScreen.movie.MovieDetailViewModel
import com.prafullkumar.moviesmate.ui.mainScreen.movie.reviewScreen.ReviewViewModel
import com.prafullkumar.moviesmate.ui.mainScreen.profile.ProfileRepository
import com.prafullkumar.moviesmate.ui.mainScreen.profile.ProfileVM
import com.prafullkumar.moviesmate.ui.mainScreen.profile.userReviewScreen.UserReviewRepository
import com.prafullkumar.moviesmate.ui.mainScreen.profile.userReviewScreen.UserReviewViewModel
import com.prafullkumar.moviesmate.ui.mainScreen.search.SearchViewModel
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
    single<MoviesRepo> { MoviesRepositoryImpl() }
    single<MovieDetailRepo> { MovieDetailRepoImpl() }
    single<FirebaseFirestore> { FirebaseFirestore.getInstance() }
    single<ReviewRepo> { ReviewRepoImpl() }
    single { ProfileRepository() }
    single<FirebaseAuth> { FirebaseAuth.getInstance() }
    viewModel { HomeViewModel() }
    viewModel { AuthViewModel() }
    viewModel { ReviewViewModel(get()) }
    viewModel { SearchViewModel() }
    viewModel { MovieDetailViewModel(get()) }
    viewModel { ProfileVM() }
    viewModel { MovieListViewModel(get()) }


    single { UserReviewRepository() }
    viewModel { UserReviewViewModel() }
}