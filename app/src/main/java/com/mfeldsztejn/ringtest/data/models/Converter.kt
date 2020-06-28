package com.mfeldsztejn.ringtest.data.models

object Converter {

    fun postDtoToPost(postDTO: PostDTO): Post {
        with(postDTO.data) {
            val image = preview?.images?.firstOrNull()?.source?.run { Image(url, width, height) }
            return Post(
                subreddit,
                domain,
                thumbnail,
                author,
                name,
                title,
                comments,
                createdUtc * 1000, // Reddit has the timestamp in seconds and we want millis
                text,
                url,
                stickied,
                image
            )
        }
    }

    fun updatePostWithDto(postById: Post, postDTO: PostDTO): Post {
        return postDtoToPost(postDTO).copy(
            isRead = postById.isRead
        )
    }
}