package com.prafullkumar.moviesmate.ui.mainScreen.home

// screens/HomeScreen.kt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
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
import com.prafullkumar.moviesmate.MainAppRoutes
import com.prafullkumar.moviesmate.model.Movies
import com.prafullkumar.moviesmate.model.detail.MovieDetail
import com.prafullkumar.moviesmate.ui.mainScreen.MovieCard
import com.prafullkumar.moviesmate.ui.mainScreen.SectionHeader
import com.prafullkumar.moviesmate.ui.mainScreen.categoryScreen.Type
import com.prafullkumar.moviesmate.utils.Resource
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: HomeViewModel,
    navHostController: NavHostController
) {
    val latestMovies by viewModel.latestMovies.collectAsState()
    val latestSeries by viewModel.latestSeries.collectAsState()
    val recentlyViewed by viewModel.recentlyViewed.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("MovieMate")
            }, actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Default.Search, "Search")
                }
                IconButton(onClick = onProfileClick) {
                    Icon(Icons.Default.Person, "Profile")
                }
            })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Section(title = "Latest Releases",
                resource = latestMovies,
                navController = navHostController,
                onSeeAllClick = {
                    navHostController.navigate(
                        MainAppRoutes.CategoryScreen(
                            "movie", Type.MOVIE, Calendar.getInstance().get(Calendar.YEAR)
                        )
                    )
                })
            Section(title = "Latest Series",
                resource = latestSeries,
                navController = navHostController,
                onSeeAllClick = {
                    navHostController.navigate(
                        MainAppRoutes.CategoryScreen(
                            "series", Type.SERIES, Calendar.getInstance().get(Calendar.YEAR)
                        )
                    )
                })

            // Categories/Genres
            SectionHeader(title = "Categories", true, onSeeAllClick = {
                navHostController.navigate(MainAppRoutes.CategorySelectionScreen)
            })
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val genres = listOf("Action", "Comedy", "Drama", "Horror", "Romance")
                items(genres.size) { index ->
                    AssistChip(onClick = {
                        navHostController.navigate(
                            MainAppRoutes.CategoryScreen(
                                genres[index], type = Type.GENRE
                            )
                        )
                    }, label = { Text(genres[index]) })
                }
            }

            SectionHeader(title = "Recently Viewed", false, onSeeAllClick = { })
            when (recentlyViewed) {
                is Resource.Empty -> {
                    Text(
                        text = "No data found", modifier = Modifier.padding(16.dp)
                    )
                }

                is Resource.Error -> {
                    Text(
                        text = (recentlyViewed as Resource.Error).message,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Resource.Loading -> {
                    CircularProgressIndicator()
                }

                is Resource.Success -> {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        reverseLayout = true
                    ) {
                        items((recentlyViewed as Resource.Success<List<MovieDetail>>).data) {
                            MovieCard(
                                imageUrl = it.Poster ?: "",
                                title = it.Title ?: "N/A",
                                rating = 4.5f,
                                navController = navHostController,
                                imdbId = it.imdbID!!
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun Section(
    title: String,
    resource: Resource<Movies>,
    onSeeAllClick: () -> Unit = {},
    navController: NavHostController
) {
    SectionHeader(
        title = title, true, onSeeAllClick = onSeeAllClick
    )
    when (resource) {
        is Resource.Empty -> {
            Text(
                text = "No data found", modifier = Modifier.padding(16.dp)
            )
        }

        is Resource.Error -> {
            Text(
                text = resource.message, modifier = Modifier.padding(16.dp)
            )
        }

        Resource.Loading -> {
            CircularProgressIndicator()
        }

        is Resource.Success -> {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(resource.data.search, key = {
                    it.imdbID + it.title
                }) {
                    MovieCard(
                        imageUrl = it.poster,
                        title = it.title.capitalize(Locale.US),
                        rating = 4.5f,
                        navController = navController,
                        imdbId = it.imdbID
                    )
                }
            }
        }
    }
}