package com.mfeldsztejn.ringtest.data.source.remote

import com.mfeldsztejn.ringtest.data.models.PostsResponseDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditAPI {
    @GET("/r/{subreddit}/hot.json?raw_json=1")
    suspend fun getPosts(
        @Path("subreddit") subreddit: String,
        @Query("after") after: String?,
        @Query("before") before: String?,
        @Query("limit") limit: Int
    ): PostsResponseDTO
}