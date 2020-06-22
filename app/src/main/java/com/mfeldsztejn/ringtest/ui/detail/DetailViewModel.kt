package com.mfeldsztejn.ringtest.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.mfeldsztejn.ringtest.data.source.PostsRepository

class DetailViewModel(
    private val postId: Int,
    private val postsRepository: PostsRepository
) : ViewModel() {

    val post = liveData {
        emit(postsRepository.getPostById(postId))
    }
}