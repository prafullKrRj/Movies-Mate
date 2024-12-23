package com.prafullkumar.moviesmate.ui.mainScreen.categoryScreen

import androidx.lifecycle.ViewModel
import com.prafullkumar.moviesmate.MainAppRoutes
import com.prafullkumar.moviesmate.domain.MoviesRepo
import com.prafullkumar.moviesmate.utils.API_KEY
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Locale

enum class Type {
    MOVIE, SERIES, GENRE
}

class MovieListViewModel(
    val category: MainAppRoutes.CategoryScreen
) : ViewModel(), KoinComponent {
    private val moviesRepo: MoviesRepo by inject()

    val shows = moviesRepo.getMoviesStream(
        API_KEY,
        category.category,
        if (category.type == Type.GENRE) "" else category.type.name.lowercase(Locale.ROOT),
        if (category.year == 0) null else category.year
    )
}