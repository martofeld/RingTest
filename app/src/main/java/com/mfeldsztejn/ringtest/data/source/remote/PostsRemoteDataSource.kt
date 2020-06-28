package com.mfeldsztejn.ringtest.data.source.remote

import com.mfeldsztejn.ringtest.util.wrapEspressoIdlingResource

class PostsRemoteDataSource(
    private val api: RedditAPI
) {

    suspend fun getPosts(subreddit: String, after: String?, before: String?, limit: Int) =
        wrapEspressoIdlingResource { api.getPosts(subreddit, after, before, limit).data }
}