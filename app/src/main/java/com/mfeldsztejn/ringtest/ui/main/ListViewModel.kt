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
    }

    init {
        if (!savedState.contains(KEY_SUBREDDIT)) {
            savedState.set(KEY_SUBREDDIT, storage.getSubreddit())
        }
    }

    val currentSubreddit: LiveData<String>
        get() = savedState.getLiveData(KEY_SUBREDDIT)

    val posts = Transformations.switchMap(currentSubreddit) {
        storage.saveSubreddit(it)
        repository
            .postsOfSubreddit(it, 30)
            .cachedIn(viewModelScope)
    }

    fun showSubreddit(subreddit: String?) {
        if (subreddit == null || currentSubreddit.value == subreddit) return

        savedState.set(KEY_SUBREDDIT, subreddit)
    }

    fun markPostAsRead(name: String) {
        viewModelScope.launch {
            repository.markPostAsRead(name)
        }
    }

    fun removePost(name: String) {
        viewModelScope.launch {
            repository.removePost(name)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            currentSubreddit.value?.let {
                repository.clearAll(it)
            }
        }
    }
}