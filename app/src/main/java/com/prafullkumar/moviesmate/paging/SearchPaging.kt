package com.prafullkumar.moviesmate.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.prafullkumar.moviesmate.domain.ApiService
import com.prafullkumar.moviesmate.model.Search
import com.prafullkumar.moviesmate.utils.API_KEY

class SearchPagingSource(
    private val apiService: ApiService,
    private val searchQuery: String,
    private val selectedFilter: String
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
            val response = apiService.getMovieList(
                apiKey = API_KEY,
                search = searchQuery,
                type = if (selectedFilter.lowercase() == "all") "" else selectedFilter,
                page = position
            )
            if (response.isSuccessful) {
                val movies = response.body()!!.search
                LoadResult.Page(
                    data = movies,
                    prevKey = if (position == 1) null else position - 1,
                    nextKey = if (movies.isEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(Exception(response.message()))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
