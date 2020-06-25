package com.mfeldsztejn.ringtest.ui.main

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.mfeldsztejn.ringtest.Storage
import com.mfeldsztejn.ringtest.data.source.PostsRepository
import kotlinx.coroutines.launch

class ListViewModel(
    private val storage: Storage,
    private val repository: PostsRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {

    companion object {
        const val KEY_SUBREDDIT = "subreddit"
        const val DEFAULT_SUBREDDIT = "kotlin"
    }

    init {
        if (!savedState.contains(KEY_SUBREDDIT)) {
            savedState.set(KEY_SUBREDDIT, storage.getSubreddit())
        }
    }

    val currentSubrredit: LiveData<String>
        get() = savedState.getLiveData(KEY_SUBREDDIT)

    val posts = Transformations.switchMap(currentSubrredit) {
        storage.saveSubreddit(it)
        repository
            .postsOfSubreddit(it, 30)
            .cachedIn(viewModelScope)
    }

    fun showSubreddit(subreddit: String?) {
        val finalSubreddit = subreddit ?: DEFAULT_SUBREDDIT
        if (currentSubrredit.value == finalSubreddit) return

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

    fun clearAll() {
        viewModelScope.launch {
            currentSubrredit.value?.let {
                repository.clearAll(it)
            }
        }
    }
}