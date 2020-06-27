package com.mfeldsztejn.ringtest.data.source

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.mfeldsztejn.ringtest.data.PageKeyedRemoteMediator
import com.mfeldsztejn.ringtest.data.source.local.PostsLocalDataSource
import com.mfeldsztejn.ringtest.data.source.remote.PostsRemoteDataSource

class PostsRepositoryImpl(
    private val localDataSource: PostsLocalDataSource,
    private val remoteDataSource: PostsRemoteDataSource
) : PostsRepository {

    override suspend fun markPostAsRead(id: Int) {
        localDataSource.markPostAsRead(id)
    }

    override suspend fun removePost(id: Int) {
        localDataSource.removePost(id)
    }

    override suspend fun clearAll(subreddit: String) {
        localDataSource.clearAll(subreddit)
    }

    override suspend fun getPostById(postId: Int) = localDataSource.getPostById(postId)

    override fun postsOfSubreddit(subreddit: String, pageSize: Int) = Pager(
        config = PagingConfig(pageSize = pageSize),
        remoteMediator = PageKeyedRemoteMediator(localDataSource, remoteDataSource, subreddit)
    ) {
        localDataSource.postsBySubreddit(subreddit)
    }.liveData
}