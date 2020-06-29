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
    val subreddit: String,
    val stickied: Boolean
)

data class PostDetailDTO(
    @SerializedName("selftext_html")
    val text: String,
    val url: String,
    val preview: PreviewDTO?
)

data class PreviewDTO(val images: List<ImagesDTO>?)

data class ImagesDTO(val source: SourceDTO?)

data class SourceDTO(val url: String, val width: Int, val height: Int)

data class PostDTO<T>(val kind: String, val data: T)

data class PostsResponseDTO<T>(val kind: String, val data: PostsDataDTO<T>)

data class PostsDataDTO<T>(
    val modhash: String,
    val children: List<PostDTO<T>>,
    val after: String?,
    val before: String?,
    val count: Int?
)