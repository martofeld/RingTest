package com.mfeldsztejn.ringtest.data.source.local

import androidx.room.withTransaction
import com.mfeldsztejn.ringtest.data.models.Post
import com.mfeldsztejn.ringtest.data.models.entities.SubredditRemoteKey

class PostsLocalDataSource(private val database: PostsDatabase) {

    private val keyDao by lazy { database.keyDao }
    private val postDao by lazy { database.postDao }

    suspend fun getRemoteKeyForSubreddit(subredditName: String) = database.withTransaction {
        keyDao.remoteKeyByPost(subredditName)
    }

    suspend fun updateBySubreddit(
        subredditName: String,
        newItems: List<Post>,
        after: String?,
        isRefreshing: Boolean
    ) {
        database.withTransaction {
            if (isRefreshing) {
                postDao.deleteBySubreddit(subredditName)
                keyDao.deleteBySubreddit(subredditName)
            }

            keyDao.insert(SubredditRemoteKey(subredditName, after))
            postDao.insertAll(newItems)
        }
    }

    fun postsBySubreddit(subReddit: String) = postDao.postsBySubreddit(subReddit)

}