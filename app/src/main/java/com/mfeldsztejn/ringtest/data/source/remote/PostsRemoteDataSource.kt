package com.mfeldsztejn.ringtest.data.source.remote

class PostsRemoteDataSource(
    private val api: RedditAPI
) {

    suspend fun getPosts(subreddit: String, after: String?, before: String?, limit: Int) =
        api.getPosts(subreddit, after, before, limit)
}