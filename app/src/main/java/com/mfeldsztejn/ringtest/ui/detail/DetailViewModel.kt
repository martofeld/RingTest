package com.mfeldsztejn.ringtest.ui.detail

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.mfeldsztejn.ringtest.data.models.PostDetailDTO
import com.mfeldsztejn.ringtest.data.source.PostsRepository

class DetailViewModel(
    private val postName: String,
    private val postsRepository: PostsRepository
) : ViewModel() {

    val post = liveData {
        emit(postsRepository.getPostByName(postName)!!)
    }

    val postDetail = Transformations.switchMap(post) {
        liveData {
            emit(postsRepository.getPostDetails(it.subreddit, it.name))
        }
    }
}