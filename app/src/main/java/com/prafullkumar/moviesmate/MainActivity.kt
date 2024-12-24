package com.prafullkumar.moviesmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import com.prafullkumar.moviesmate.ui.auth.AuthScreen
import com.prafullkumar.moviesmate.ui.mainScreen.categoryScreen.MovieListScreen
import com.prafullkumar.moviesmate.ui.mainScreen.categoryScreen.Type
import com.prafullkumar.moviesmate.ui.mainScreen.categoryScreen.categorrySelection.CategorySelectionScreen
import com.prafullkumar.moviesmate.ui.mainScreen.home.HomeScreen
import com.prafullkumar.moviesmate.ui.mainScreen.movie.MovieScreen
import com.prafullkumar.moviesmate.ui.mainScreen.movie.reviewScreen.ReviewScreen
import com.prafullkumar.moviesmate.ui.mainScreen.profile.ProfileScreen
import com.prafullkumar.moviesmate.ui.mainScreen.profile.userReviewScreen.UserReviewScreen
import com.prafullkumar.moviesmate.ui.mainScreen.search.SearchScreen
import com.prafullkumar.moviesmate.ui.theme.MoviesMateTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


sealed class AppRoutes {
    @Serializable
    data object AuthScreen : AppRoutes()

    @Serializable
    data object Application : AppRoutes()
}

class MainActivity : ComponentActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoviesMateTheme {
                val navController = rememberNavController()
                val startDestination = if (firebaseAuth.currentUser == null) {
                    AppRoutes.AuthScreen
                } else {
                    AppRoutes.Application
                }
                NavHost(navController, startDestination = startDestination) {
                    composable<AppRoutes.AuthScreen> {
                        AuthScreen(viewModel = getViewModel(), navController = navController)
                    }
                    mainAppNavigation(navController)
                }
            }
        }
    }
}

fun NavGraphBuilder.mainAppNavigation(navController: NavHostController) {
    navigation<AppRoutes.Application>(startDestination = MainAppRoutes.HomeScreen) {
        composable<MainAppRoutes.HomeScreen> {
            HomeScreen(viewModel = getViewModel(), onSearchClick = {
                navController.navigate(MainAppRoutes.SearchScreen)
            }, onProfileClick = {
                navController.navigate(MainAppRoutes.ProfileScreen)
            }, navHostController = navController)
        }
        composable<MainAppRoutes.SearchScreen> {
            SearchScreen(
                viewModel = getViewModel(), navController = navController
            )
        }
        composable<MainAppRoutes.MovieDetailScreen> {
            val movie = it.toRoute<MainAppRoutes.MovieDetailScreen>()
            MovieScreen(viewModel = koinViewModel { parametersOf(movie) }, navController)
        }
        composable<MainAppRoutes.ProfileScreen> {
            ProfileScreen(viewModel = getViewModel(), navController)
        }
        composable<MainAppRoutes.CategoryScreen> {
            val category = it.toRoute<MainAppRoutes.CategoryScreen>()
            MovieListScreen(
                viewModel = koinViewModel { parametersOf(category) }, navController
            )
        }
        composable<MainAppRoutes.CategorySelectionScreen> {
            CategorySelectionScreen(Modifier, navController)
        }
        composable<MainAppRoutes.ReviewScreen> {
            val review = it.toRoute<MainAppRoutes.ReviewScreen>()
            ReviewScreen(viewModel = koinViewModel { parametersOf(review) }, navController)
        }
        composable<MainAppRoutes.UserReviews> {
            UserReviewScreen(viewModel = koinViewModel(), navController)
        }
    }
}

sealed interface MainAppRoutes {
    @Serializable
    data object HomeScreen : MainAppRoutes

    @Serializable
    data object SearchScreen : MainAppRoutes

    @Serializable
    data object ProfileScreen : MainAppRoutes

    @Serializable
    data class MovieDetailScreen(val id: String, val name: String) : MainAppRoutes

    @Serializable
    data class CategoryScreen(val category: String, val type: Type, val year: Int = 0) :
        MainAppRoutes

    @Serializable
    data object CategorySelectionScreen : MainAppRoutes

    @Serializable
    data class ReviewScreen(val id: String, val title: String, val poster: String) : MainAppRoutes

    @Serializable
    data object UserReviews : MainAppRoutes
}