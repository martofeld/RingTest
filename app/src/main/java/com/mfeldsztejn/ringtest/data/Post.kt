package com.mfeldsztejn.ringtest.data

import com.google.gson.annotations.SerializedName

data class PostData(
    val domain: String,
    val thumbnail: String?,
    val author: String,
    val name: String,
    val title: String,
    @SerializedName("num_comments")
    val comments: Int,
    @SerializedName("created_utc")
    val createdUtc: Long
)

data class Post(val kind: String, val data: PostData)

data class PostsResponse(val type: String, val data: PostsData)

data class PostsData(
    val modhash: String,
    val children: List<Post>,
    val after: String?,
    val before: String?,
    val count: Int?
)