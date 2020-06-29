package com.mfeldsztejn.ringtest.data.source

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.mfeldsztejn.ringtest.data.PageKeyedRemoteMediator
import com.mfeldsztejn.ringtest.data.models.Converter
import com.mfeldsztejn.ringtest.data.models.Detail
import com.mfeldsztejn.ringtest.data.models.PostDetailDTO
import com.mfeldsztejn.ringtest.data.source.local.PostsLocalDataSource
import com.mfeldsztejn.ringtest.data.source.remote.PostsRemoteDataSource

class PostsRepositoryImpl(
    private val localDataSource: PostsLocalDataSource,
    private val remoteDataSource: PostsRemoteDataSource
) : PostsRepository {

    override suspend fun markPostAsRead(name: String) {
        localDataSource.markPostAsRead(name)
    }

    override suspend fun removePost(name: String) {
        localDataSource.removePost(name)
    }

    override suspend fun clearAll(subreddit: String) {
        localDataSource.clearAll(subreddit)
    }

    override suspend fun getPostByName(name: String) = localDataSource.getPostByName(name)

    override fun postsOfSubreddit(subreddit: String, pageSize: Int) = Pager(
        config = PagingConfig(pageSize = pageSize),
        remoteMediator = PageKeyedRemoteMediator(localDataSource, remoteDataSource, subreddit)
    ) {
        localDataSource.postsBySubreddit(subreddit)
    }.liveData

    override suspend fun getPostDetails(subreddit: String, name: String): Detail {
        return Converter.detailDtoToDetail(remoteDataSource.getPostDetails(subreddit, name))
    }
}