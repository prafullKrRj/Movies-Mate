package com.prafullkumar.moviesmate.ui.mainScreen.categoryScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.prafullkumar.moviesmate.ui.mainScreen.MovieCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(viewModel: MovieListViewModel, navHostController: NavHostController) {
    val shows = viewModel.shows.collectAsLazyPagingItems()
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = if (viewModel.category.type == Type.GENRE) viewModel.category.category else "Latest " + viewModel.category.type.name.lowercase())
        }, navigationIcon = {
            IconButton(onClick = {
                navHostController.popBackStack()
            }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
            }
        })
    }) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (shows.loadState.refresh) {
                is androidx.paging.LoadState.Loading -> {
                    CircularProgressIndicator()
                }

                is androidx.paging.LoadState.Error -> {
                    Text(text = "Error")
                }

                is androidx.paging.LoadState.NotLoading -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(150.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(shows.itemCount) { index ->
                            shows[index]?.let { show ->
                                MovieCard(
                                    imageUrl = show.poster ?: "",
                                    title = show.title,
                                    rating = 4.5f,
                                    imdbId = show.imdbID,
                                    navController = navHostController
                                )
                            }
                        }
                        if (shows.loadState.append is androidx.paging.LoadState.Loading) {
                            item {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    contentAlignment = Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}