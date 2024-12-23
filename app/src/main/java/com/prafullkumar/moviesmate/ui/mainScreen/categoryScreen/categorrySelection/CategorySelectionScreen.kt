package com.prafullkumar.moviesmate.ui.mainScreen.categoryScreen.categorrySelection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prafullkumar.moviesmate.MainAppRoutes
import com.prafullkumar.moviesmate.ui.mainScreen.categoryScreen.Type


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionScreen(modifier: Modifier = Modifier, navController: NavController) {
    Scaffold(modifier = modifier, topBar = {
        TopAppBar(title = {
            Text("Select a Category")
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
        })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                movieCategories.forEach { category ->
                    AssistChip(
                        onClick = {
                            navController.navigate(
                                MainAppRoutes.CategoryScreen(
                                    category,
                                    Type.GENRE
                                )
                            )
                        },
                        label = { Text(category) }
                    )
                }
            }
        }
    }
}

val movieCategories = listOf(
    "Action",
    "Adventure",
    "Comedy",
    "Drama",
    "Horror",
    "Science Fiction",
    "Fantasy",
    "Romance",
    "Thriller",
    "Mystery",
    "Crime",
    "Documentary",
    "Animation",
    "Biography",
    "Musical",
    "Family",
    "War",
    "Western",
    "Historical",
    "Sports"
)