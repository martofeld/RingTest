package com.mfeldsztejn.ringtest.data.models

object Converter {

    fun detailDtoToDetail(detailDTO: PostDTO<PostDetailDTO>?): Detail {
        return detailDTO?.data?.let {
            val image = it.preview?.images?.firstOrNull()?.source?.run { Image(url, width, height) }
            Detail(it.text, it.url, image)
        } ?: Detail()
    }

    fun postDtoToPost(postDTO: PostDTO<PostDataDTO>): Post {
        with(postDTO.data) {
            return Post(
                subreddit,
                domain,
                thumbnail,
                author,
                name,
                title,
                comments,
                createdUtc * 1000, // Reddit has the timestamp in seconds and we want millis
                stickied
            )
        }
    }

    fun updatePostWithDto(postById: Post, postDTO: PostDTO<PostDataDTO>): Post {
        return postDtoToPost(postDTO).copy(
            isRead = postById.isRead
        )
    }
}