package com.mfeldsztejn.ringtest.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mfeldsztejn.ringtest.data.source.PostsRepository
import kotlinx.coroutines.launch

class ListViewModel(
    private val repository: PostsRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {

    companion object {
        const val KEY_SUBREDDIT = "subreddit"
        const val DEFAULT_SUBREDDIT = "kotlin"
    }

    init {
        if (!savedState.contains(KEY_SUBREDDIT)) {
            savedState.set(KEY_SUBREDDIT, DEFAULT_SUBREDDIT)
        }
    }

    val posts = Transformations.switchMap(savedState.getLiveData<String>(KEY_SUBREDDIT)) {
        repository.postsOfSubreddit(it, 30)
    }

    private fun shouldShowSubreddit(
        subreddit: String
    ) = savedState.get<String>(KEY_SUBREDDIT) != subreddit

    fun showSubreddit(subreddit: String) {
        if (!shouldShowSubreddit(subreddit)) return

        savedState.set(KEY_SUBREDDIT, subreddit)
    }

    fun markPostAsRead(id: Int) {
        viewModelScope.launch {
            repository.markPostAsRead(id)
        }
    }

    fun removePost(id: Int) {
        viewModelScope.launch {
            repository.removePost(id)
        }
    }
}