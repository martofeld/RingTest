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

    override fun postsOfSubreddit(subreddit: String, pageSize: Int) = Pager(
        config = PagingConfig(pageSize = 25, initialLoadSize = 100),
        remoteMediator = PageKeyedRemoteMediator(localDataSource, remoteDataSource, subreddit)
    ) {
        localDataSource.postsBySubreddit(subreddit)
    }.liveData
}