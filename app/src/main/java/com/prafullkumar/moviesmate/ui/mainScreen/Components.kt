package com.prafullkumar.moviesmate.ui.mainScreen

// components/MovieCard.kt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.prafullkumar.moviesmate.MainAppRoutes

@Composable
fun MovieCard(
    imageUrl: String,
    title: String,
    rating: Float,
    imdbId: String,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    Card(
        modifier = modifier
            .width(150.dp)
            .height(220.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .clickable {
                    navController.navigate(MainAppRoutes.MovieDetailScreen(imdbId, title))
                }) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(
                    text = "★ $rating",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

// components/SectionHeader.kt
@Composable
fun SectionHeader(
    title: String,
    showSeeAll: Boolean,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        if (showSeeAll) {
            TextButton(onClick = onSeeAllClick) {
                Text("See all")
            }
        }
    }
}
