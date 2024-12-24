package com.prafullkumar.moviesmate.ui.mainScreen.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.prafullkumar.moviesmate.MainAppRoutes
import com.prafullkumar.moviesmate.ui.mainScreen.MovieCard
import com.prafullkumar.moviesmate.utils.Resource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileVM,
    navController: NavHostController
) {
    var showSettingsDialog by remember { mutableStateOf(false) }
    val userState by viewModel.userProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (userState) {
                Resource.Empty -> {
                    Text(
                        text = "No user found",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is Resource.Error -> {
                    Text(
                        text = (userState as Resource.Error).message,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Resource.Loading -> {
                    CircularProgressIndicator()
                }

                is Resource.Success -> {
                    val profile = (userState as Resource.Success<UserState>).data
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // User Info Section
                        item {
                            UserInfoSection(profile.userProfile)
                        }

                        item {
                            RecentlyViewedSection(
                                viewModel,
                                profile,
                                navController
                            )
                        }

                        item {

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Your Reviews",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                                IconButton(
                                    onClick = {
                                        navController.navigate(
                                            MainAppRoutes.UserReviews
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null
                                    )
                                }
                            }
                        }

                    }

                    if (showSettingsDialog) {
                        SettingsDialog(
                            onDismiss = { showSettingsDialog = false },
                            onUsernameChange = viewModel::changeUsername,
                            onPasswordChange = viewModel::changePassword
                        )
                    }
                }

            }
        }
    }
}

@Composable
private fun UserInfoSection(userProfile: UserProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = userProfile.username.first().toString().uppercase(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userProfile.username,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "${userProfile.reviewedShows.size} reviews",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RecentlyViewedSection(
    viewModel: ProfileVM,
    userDetails: UserState,
    navController: NavHostController
) {
    var showRecentlyViewed by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showRecentlyViewed = !showRecentlyViewed }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recently Viewed",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = {
                viewModel.getRecentlyViewedShows()
                showRecentlyViewed = !showRecentlyViewed
            }) {
                Icon(
                    imageVector = if (showRecentlyViewed) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }

        if (showRecentlyViewed) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (userDetails.recentlyViewedLoading) {
                    items(10) {
                        Card(
                            modifier = Modifier
                                .width(150.dp)
                                .height(220.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column {
                                CircularProgressIndicator()
                            }
                        }
                    }
                } else if (!userDetails.recentlyViewed.isNullOrEmpty()) {
                    items(userDetails.recentlyViewed) { movie ->
                        MovieCard(
                            imageUrl = movie.Poster ?: "",
                            title = movie.Title ?: "",
                            rating = 4.5f,
                            imdbId = movie.imdbID ?: "",
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsDialog(
    onDismiss: () -> Unit,
    onUsernameChange: () -> Unit,
    onPasswordChange: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onUsernameChange,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Change Username")
                }

                FilledTonalButton(
                    onClick = onPasswordChange,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Change Password")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

//@Composable
//fun ProfileScreen(viewModel: ProfileVM) {
//    var username by remember { mutableStateOf(viewModel.username) }
//    var password by remember { mutableStateOf(viewModel.password) }
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        Text(text = "Profile", style = MaterialTheme.typography.h4)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        TextField(
//            value = username,
//            onValueChange = { username = it },
//            label = { Text("Username") }
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        TextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            visualTransformation = PasswordVisualTransformation()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onClick = { viewModel.updateCredentials(username, password) }) {
//            Text("Update Credentials")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(text = "Your Reviews", style = MaterialTheme.typography.h5)
//
//        LazyColumn {
//            items(viewModel.reviews) { review ->
//                Text(text = review)
//                Divider()
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(text = "Recently Viewed", style = MaterialTheme.typography.h5)
//
//        LazyColumn {
//            items(viewModel.recentlyViewed) { item ->
//                Text(text = item)
//                Divider()
//            }
//        }
//    }
//}