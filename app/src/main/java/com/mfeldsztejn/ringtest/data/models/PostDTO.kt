package com.mfeldsztejn.ringtest.data.models

import com.google.gson.annotations.SerializedName

data class PostDataDTO(
    val domain: String,
    val thumbnail: String?,
    val author: String,
    val name: String,
    val title: String,
    @SerializedName("num_comments")
    val comments: Int,
    @SerializedName("created_utc")
    val createdUtc: Long,
    val subreddit: String
)

data class PostDTO(val kind: String, val data: PostDataDTO)

data class PostsResponseDTO(val type: String, val data: PostsDataDTO)

data class PostsDataDTO(
    val modhash: String,
    val children: List<PostDTO>,
    val after: String?,
    val before: String?,
    val count: Int?
)