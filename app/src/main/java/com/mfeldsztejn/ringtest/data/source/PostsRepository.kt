package com.mfeldsztejn.ringtest.data.source

import com.mfeldsztejn.ringtest.data.Post

interface PostsRepository {
    suspend fun getPosts(): List<Post>
    suspend fun markPostAsRead(id: Int)
}