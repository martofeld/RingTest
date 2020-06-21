package com.mfeldsztejn.ringtest.data.models

object Converter {

    fun postDtoToPost(postDTO: PostDTO): Post {
        with(postDTO.data) {
            return Post(subreddit, domain, thumbnail, author, name, title, comments, createdUtc)
        }
    }
}