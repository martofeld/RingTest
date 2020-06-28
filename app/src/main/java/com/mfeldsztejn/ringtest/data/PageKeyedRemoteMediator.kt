package com.mfeldsztejn.ringtest.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.mfeldsztejn.ringtest.data.models.Converter
import com.mfeldsztejn.ringtest.data.models.Post
import com.mfeldsztejn.ringtest.data.source.local.PostsLocalDataSource
import com.mfeldsztejn.ringtest.data.source.remote.PostsRemoteDataSource
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PageKeyedRemoteMediator(
    private val localDataSource: PostsLocalDataSource,
    private val remoteDataSource: PostsRemoteDataSource,
    private val subredditName: String
) : RemoteMediator<Int, Post>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Post>
    ): MediatorResult {
        try {
            // Get the closest item from PagingState that we want to load data around.
            val loadKey = when (loadType) {
                REFRESH -> null
                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                APPEND -> {
                    // Query DB for SubredditRemoteKey for the subreddit.
                    // SubredditRemoteKey is a wrapper object we use to keep track of page keys we
                    // receive from the Reddit API to fetch the next or previous page.
                    val remoteKey = localDataSource.getRemoteKeyForSubreddit(subredditName)

                    // We must explicitly check if the page key is null when appending, since the
                    // Reddit API informs the end of the list by returning null for page key, but
                    // passing a null key to Reddit API will fetch the initial page.
                    if (remoteKey.nextPageKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    remoteKey.nextPageKey
                }
            }

            val limit = when (loadType) {
                REFRESH -> state.config.initialLoadSize
                else -> state.config.pageSize
            }
            val data = remoteDataSource.getPosts(
                subreddit = subredditName,
                after = loadKey,
                before = null,
                limit = limit
            )

            val items = data.children
                .map { Converter.postDtoToPost(it) }
                // Sometimes the reddit api returns more items than required, so drop the extra ones
                .let { if ((it.size > limit)) it.take(limit) else it }

            localDataSource.updateBySubreddit(subredditName, items, data.after, loadType == REFRESH)

            return MediatorResult.Success(endOfPaginationReached = items.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}
