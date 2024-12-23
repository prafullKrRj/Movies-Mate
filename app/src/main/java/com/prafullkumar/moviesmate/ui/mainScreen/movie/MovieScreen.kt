package com.prafullkumar.moviesmate.ui.mainScreen.movie

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.prafullkumar.moviesmate.MainAppRoutes
import com.prafullkumar.moviesmate.R
import com.prafullkumar.moviesmate.model.MovieWithReviews
import com.prafullkumar.moviesmate.model.detail.MovieDetail
import com.prafullkumar.moviesmate.utils.Resource


@Composable
fun MovieScreen(
    viewModel: MovieDetailViewModel, navController: NavController
) {

    val movieDetail by viewModel.movieDetail.collectAsState()
    when (movieDetail) {
        is Resource.Empty -> {
            Text(
                text = "No data found",
                modifier = Modifier.padding(16.dp)
            )
        }

        is Resource.Error -> {
            Text(
                text = (movieDetail as Resource.Error).message,
                modifier = Modifier.padding(16.dp)
            )
        }

        Resource.Loading -> {
            CircularProgressIndicator()
        }

        is Resource.Success -> {
            MovieDetailSuccess(
                movieDetail = (movieDetail as Resource.Success).data,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun MovieDetailSuccess(
    movieDetail: MovieWithReviews,
    navController: NavController,
    viewModel: MovieDetailViewModel
) {
    val uriHandler = LocalUriHandler.current
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = movieDetail.movie.Poster,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 20.dp),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                movieDetail.movie.Title?.let {
                    DetailTopAppBar(
                        title = it,
                        onBackPressed = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(scrollState)
            ) {
                // Header Section
                MovieHeader(movieDetail.movie)

                // Content Sections
                MovieInfo(movieDetail.movie)

                PlotSection(movieDetail.movie)

                CastSection(movieDetail.movie)

                TechnicalDetails(movieDetail.movie)

                RatingsSection(movieDetail.movie)

                MovieMateRatingSection(
                    avgRating = movieDetail.avgRating,
                    totalRatings = movieDetail.totalRates,
                    onSeeReviewsClick = {
                        movieDetail.movie.imdbID?.let {
                            navController.navigate(
                                MainAppRoutes.ReviewScreen(
                                    it,
                                    movieDetail.movie.Title ?: ""
                                )
                            )
                        }
                    }
                )
                // IMDB Button
                ImdbButton(
                    imdbId = viewModel.movie.id,
                    onImdbClick = { imdbUrl ->
                        uriHandler.openUri(imdbUrl)
                    }
                )
            }
        }
    }
}

@Composable
fun MovieMateRatingSection(
    avgRating: Double,
    totalRatings: Long,
    onSeeReviewsClick: () -> Unit,
) {
    DetailSection(
        title = "MovieMate Ratings"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large Rating Display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (totalRatings == 0L) "N/A" else String.format("%.1f", avgRating),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (totalRatings > 0) {
                            repeat(10) { index ->
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = when {
                                        index < avgRating -> Color(0xFFf3ce13)
                                        index < avgRating + 0.5 -> Color(0xFFf3ce13).copy(alpha = 0.5f)
                                        else -> Color.Gray
                                    }
                                )
                            }
                        }
                    }
                    Text(
                        text = "$totalRatings ratings",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // See All Reviews Button
            OutlinedButton(
                onClick = onSeeReviewsClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("See All Reviews")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopAppBar(
    title: String,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        title = { Text("") },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun MovieHeader(movieDetail: MovieDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Poster
        Card(
            modifier = Modifier
                .width(140.dp)
                .height(200.dp)
        ) {
            AsyncImage(
                model = movieDetail.Poster,
                contentDescription = "Movie Poster",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Title and Basic Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            movieDetail.Title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            movieDetail.Runtime?.let {
                InfoChip(
                    ImageVector.vectorResource(R.drawable.outline_timer_24),
                    it
                )
            }
            movieDetail.imdbRating?.let { InfoChip(Icons.Outlined.Star, it) }
            movieDetail.Rated?.let {
                InfoChip(
                    ImageVector.vectorResource(R.drawable.baseline_movie_24),
                    it
                )
            }
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun MovieInfo(movieDetail: MovieDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Released: ${movieDetail.Released}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
        Text(
            text = "Genre: ${movieDetail.Genre}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
        Text(
            text = "Director: ${movieDetail.Director}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

@Composable
private fun PlotSection(movieDetail: MovieDetail) {
    DetailSection(
        title = "Plot",
        content = {
            movieDetail.Plot?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    )
}

@Composable
private fun CastSection(movieDetail: MovieDetail) {
    DetailSection(
        title = "Cast",
        content = {
            movieDetail.Actors?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    )
}

@Composable
private fun TechnicalDetails(movieDetail: MovieDetail) {
    DetailSection(
        title = "Technical Details",
        content = {
            movieDetail.Language?.let { DetailRow("Language", it) }
            movieDetail.Country?.let { DetailRow("Country", it) }
            movieDetail.Production?.let { DetailRow("Production", it) }
            movieDetail.BoxOffice?.let { DetailRow("Box Office", it) }
            movieDetail.DVD?.let { DetailRow("DVD Release", it) }
        }
    )
}

@Composable
private fun RatingsSection(movieDetail: MovieDetail) {
    DetailSection(
        title = "Ratings",
        content = {
            movieDetail.imdbRating?.let { RatingItem("IMDb", it) }
            movieDetail.Ratings?.forEach { rating ->
                RatingItem(rating.Source, rating.Value)
            }
        }
    )
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

@Composable
private fun RatingItem(source: String, rating: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = source,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = rating,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

@Composable
private fun ImdbButton(
    imdbId: String,
    onImdbClick: (String) -> Unit
) {
    Button(
        onClick = { onImdbClick("https://www.imdb.com/title/$imdbId") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFf3ce13),
            contentColor = Color.Black
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "View on IMDb",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                ImageVector.vectorResource(R.drawable.baseline_open_in_new_24),
                contentDescription = "Open IMDb"
            )
        }
    }
}