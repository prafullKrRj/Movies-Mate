package com.prafullkumar.moviesmate.ui.mainScreen.categoryScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.prafullkumar.moviesmate.model.Movies
import com.prafullkumar.moviesmate.ui.mainScreen.MovieCard
import com.prafullkumar.moviesmate.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(categoryViewModel: CategoryViewModel, navHostController: NavHostController) {
    val shows by categoryViewModel.shows.collectAsState()
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = categoryViewModel.category.category)
        }, navigationIcon = {
            IconButton(onClick = {
                navHostController.popBackStack()
            }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
            }
        })
    }) { paddingValues ->
        when (shows) {
            is Resource.Empty -> {
                Text(
                    text = "No data found",
                    modifier = Modifier.padding(16.dp)
                )
            }

            is Resource.Error -> {
                Text(
                    text = (shows as Resource.Error).message,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Resource.Loading -> {
                CircularProgressIndicator()
            }

            is Resource.Success -> {
                LazyVerticalGrid(
                    contentPadding = paddingValues, columns = GridCells.Adaptive(minSize = 150.dp)
                ) {
                    items((shows as Resource.Success<Movies>).data.search) { search ->
                        MovieCard(
                            modifier = Modifier.padding(8.dp),
                            imageUrl = search.poster,
                            imdbId = search.imdbID,
                            title = search.title,
                            rating = 4.5f,
                            navController = navHostController
                        )
                    }
                }
            }
        }
    }
}