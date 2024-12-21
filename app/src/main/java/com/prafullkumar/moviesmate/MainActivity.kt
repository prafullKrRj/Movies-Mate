package com.prafullkumar.moviesmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.prafullkumar.moviesmate.ui.auth.AuthScreen
import com.prafullkumar.moviesmate.ui.mainScreen.HomeScreen
import com.prafullkumar.moviesmate.ui.theme.MoviesMateTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.getViewModel


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
                    composable<AppRoutes.Application> {
                        HomeScreen(onSearchClick = {

                        }, onProfileClick = {

                        })
                    }
                }
            }
        }
    }
}