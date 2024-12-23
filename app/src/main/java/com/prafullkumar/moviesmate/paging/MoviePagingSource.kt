package com.prafullkumar.moviesmate.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.prafullkumar.moviesmate.domain.ApiService
import com.prafullkumar.moviesmate.model.Search
import com.prafullkumar.moviesmate.utils.API_KEY
import kotlinx.coroutines.delay

class MoviePagingSource(
    private val apiService: ApiService,
    private val apiKey: String,
    private val type: String,
    private val search: String,
    private val year: Int?
) : PagingSource<Int, Search>() {
    override fun getRefreshKey(state: PagingState<Int, Search>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Search> {
        val position = params.key ?: 1
        return try {
            val response = apiService.getGenericMovies(
                apiKey = API_KEY,
                search = "movie",
                type = "movie",
                page = position
            )
            delay(2000) // To show the loading state
            if (response.isSuccessful) {
                LoadResult.Page(
                    data = response.body()!!.search,
                    prevKey = if (position == 1) null else position - 1,
                    nextKey = if (response.body()!!.search.isEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(Exception(response.message()))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}