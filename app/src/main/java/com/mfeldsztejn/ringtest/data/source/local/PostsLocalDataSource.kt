package com.mfeldsztejn.ringtest.data.source.local

import androidx.room.withTransaction
import com.mfeldsztejn.ringtest.data.models.Converter
import com.mfeldsztejn.ringtest.data.models.PostDTO
import com.mfeldsztejn.ringtest.data.models.entities.SubredditRemoteKey
import com.mfeldsztejn.ringtest.util.wrapEspressoIdlingResource
import kotlinx.coroutines.*

class PostsLocalDataSource(private val database: PostsDatabase) {

    private val keyDao by lazy { database.keyDao }
    private val postDao by lazy { database.postDao }
    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    suspend fun getRemoteKeyForSubreddit(subredditName: String) = wrapEspressoIdlingResource {
        database.withTransaction {
            keyDao.remoteKeyByPost(subredditName)
        }
    }

    suspend fun updateBySubreddit(
        subredditName: String,
        newItems: List<PostDTO>,
        after: String?,
        isRefreshing: Boolean
    ) = wrapEspressoIdlingResource {
        database.withTransaction {
//            if (isRefreshing) {
//                clearAll(subredditName)
//            }

            val items = newItems.map {
                scope.async {
                    try {
                        Converter.updatePostWithDto(
                            postDao.postByName(it.data.name),
                            it
                        )
                    } catch (e: Exception) {
                        Converter.postDtoToPost(it)
                    }
                }
            }.awaitAll()

            keyDao.insert(SubredditRemoteKey(subredditName, after))
            postDao.insertAll(items)
        }
    }

    fun postsBySubreddit(subReddit: String) =
        wrapEspressoIdlingResource { postDao.postsBySubreddit(subReddit) }

    suspend fun markPostAsRead(name: String) = wrapEspressoIdlingResource {
        postDao.markPostAsRead(name)
    }

    suspend fun removePost(name: String) = wrapEspressoIdlingResource {
        postDao.removePost(name)
    }

    suspend fun clearAll(subredditName: String) {
        database.withTransaction {
            postDao.deleteBySubreddit(subredditName)
            keyDao.deleteBySubreddit(subredditName)
        }
    }

    suspend fun getPostByName(name: String) =
        wrapEspressoIdlingResource { postDao.postByName(name) }
}