package com.mfeldsztejn.ringtest.data.source

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.mfeldsztejn.ringtest.data.models.Post

interface PostsRepository {
    suspend fun markPostAsRead(id: Int)
    suspend fun removePost(id: Int)
    suspend fun clearAll(subreddit: String)
    suspend fun getPostById(postId: Int): Post
    fun postsOfSubreddit(subreddit: String, pageSize: Int): LiveData<PagingData<Post>>
}