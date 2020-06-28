package com.mfeldsztejn.ringtest.data.source

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.mfeldsztejn.ringtest.data.models.Post

interface PostsRepository {
    suspend fun markPostAsRead(name: String)
    suspend fun removePost(name: String)
    suspend fun clearAll(subreddit: String)
    suspend fun getPostByName(name: String): Post
    fun postsOfSubreddit(subreddit: String, pageSize: Int ): LiveData<PagingData<Post>>
}