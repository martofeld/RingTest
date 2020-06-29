package com.mfeldsztejn.ringtest.data.source.remote

import com.mfeldsztejn.ringtest.data.models.PostDTO
import com.mfeldsztejn.ringtest.data.models.PostDataDTO
import com.mfeldsztejn.ringtest.data.models.PostDetailDTO
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
    ): PostsResponseDTO<PostDataDTO>

    @GET("/r/{subreddit}/api/info.json?raw_json=1")
    suspend fun getPostDetail(
        @Path("subreddit") subreddit: String,
        @Query("id") name: String
    ): PostsResponseDTO<PostDetailDTO>
}