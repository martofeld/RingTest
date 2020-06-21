package com.mfeldsztejn.ringtest.data.source

import com.mfeldsztejn.ringtest.data.Post
import com.mfeldsztejn.ringtest.data.source.local.PostsLocalDataSource
import com.mfeldsztejn.ringtest.data.source.remote.PostsRemoteDataSource

class PostsRepositoryImpl(
    private val localDataSource: PostsLocalDataSource,
    private val remoteDataSource: PostsRemoteDataSource
) : PostsRepository {
    override suspend fun getPosts(): List<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun markPostAsRead(id: Int) {
        TODO("Not yet implemented")
    }
}