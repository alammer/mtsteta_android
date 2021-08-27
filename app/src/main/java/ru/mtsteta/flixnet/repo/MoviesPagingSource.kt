package ru.mtsteta.flixnet.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ru.mtsteta.flixnet.BuildConfig
import ru.mtsteta.flixnet.network.MovieRemote
import ru.mtsteta.flixnet.network.MovieRemoteService
import java.io.IOException

const val TMDB_STARTING_PAGE_INDEX = 1

@ExperimentalPagingApi
class MoviesPagingSource(
    private val service: MovieRemoteService
) : PagingSource<Int, MovieRemote>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieRemote> {
        val pageIndex = params.key ?: TMDB_STARTING_PAGE_INDEX
        val reqParams: MutableMap<String, String> = HashMap()
        reqParams["api_key"] = BuildConfig.TMDB_API_KEY
        reqParams["page"] = pageIndex.toString()
        reqParams["language"] = "ru-RU"
        reqParams["region"] = "ru"
        return try {
            val response = service.retrofitService.getPopMovieList(reqParams)
            val movies = response.body()?.responseMoviesList
            LoadResult.Page(
                data = movies ?: listOf(), prevKey = if (pageIndex == TMDB_STARTING_PAGE_INDEX) null else pageIndex - 1,
                nextKey = if (movies.isNullOrEmpty()) null else pageIndex + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieRemote>): Int? {
        return state.anchorPosition
    }
}