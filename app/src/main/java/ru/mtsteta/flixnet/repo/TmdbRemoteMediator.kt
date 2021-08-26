package ru.mtsteta.flixnet.repo
import androidx.paging.*
import androidx.room.withTransaction
import com.raywenderlich.android.redditclone.database.RedditDatabase
import com.raywenderlich.android.redditclone.models.RedditKeys
import com.raywenderlich.android.redditclone.models.RedditPost
import com.raywenderlich.android.redditclone.networking.RedditService
import retrofit2.HttpException
import ru.mtsteta.flixnet.database.MovieDataDao
import ru.mtsteta.flixnet.database.MovieLocal
import ru.mtsteta.flixnet.network.MovieRemoteService
import java.io.IOException


class TmdbRemoteMediator(
    private val networkService: MovieRemoteService,
    private val movieDataBase: MovieDataDao
) : RemoteMediator<Int, MovieLocal>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieLocal>
    ): MediatorResult {
        return try {
            val loadKey = when(loadType){
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND ->{
                    state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    getRedditKeys()
                }
            }
            val response = networkService.fetchPosts(
                loadSize = state.config.pageSize,
                after = loadKey?.after,
                before = loadKey?.before
            )
            val listing = response.body()?.data
            val redditPosts = listing?.children?.map { it.data }
            if (redditPosts != null) {
                movieDataBase.withTransaction {
                    movieDataBase.redditKeysDao()
                        .saveRedditKeys(RedditKeys(0, listing.after, listing.before))
                    movieDataBase.redditPostsDao().savePosts(redditPosts)
                }

            }
            MediatorResult.Success(endOfPaginationReached = listing?.after == null)

        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }

    }

    private suspend fun getMovieKeys(): RedditKeys? {
        return movieDataBase.redditKeysDao().getMovieKeys().firstOrNull()

    }
}