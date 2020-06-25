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
                createdUtc,
                text,
                url,
                stickied,
                image
            )
        }
    }
}