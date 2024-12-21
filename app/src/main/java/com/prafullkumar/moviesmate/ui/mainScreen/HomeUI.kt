package com.prafullkumar.moviesmate.ui.mainScreen

// screens/HomeScreen.kt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("MovieVerse")
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, "Search")
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, "Profile")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, "Search") },
                    label = { Text("Search") },
                    selected = false,
                    onClick = onSearchClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = onProfileClick
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Trending Movies Section
            SectionHeader(
                title = "Trending Movies",
                onSeeAllClick = { }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(10) {
                    MovieCard(
                        imageUrl = "https://placeholder.com/150x220",
                        title = "Movie $it",
                        rating = 4.5f
                    )
                }
            }

            // Latest Releases Section
            SectionHeader(
                title = "Latest Releases",
                onSeeAllClick = { }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(10) {
                    MovieCard(
                        imageUrl = "https://placeholder.com/150x220",
                        title = "Movie $it",
                        rating = 4.2f
                    )
                }
            }

            // Popular in Your Region
            SectionHeader(
                title = "Popular in Your Region",
                onSeeAllClick = { }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(10) {
                    MovieCard(
                        imageUrl = "https://placeholder.com/150x220",
                        title = "Movie $it",
                        rating = 4.7f
                    )
                }
            }

            // Categories/Genres
            SectionHeader(
                title = "Categories",
                onSeeAllClick = { }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val genres = listOf("Action", "Comedy", "Drama", "Horror", "Romance")
                items(genres.size) { index ->
                    AssistChip(
                        onClick = { },
                        label = { Text(genres[index]) }
                    )
                }
            }

            // Recently Viewed
            SectionHeader(
                title = "Recently Viewed",
                onSeeAllClick = { }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(5) {
                    MovieCard(
                        imageUrl = "https://placeholder.com/150x220",
                        title = "Movie $it",
                        rating = 4.0f
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}