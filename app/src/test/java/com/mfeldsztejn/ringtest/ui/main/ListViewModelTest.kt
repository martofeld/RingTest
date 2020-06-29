package com.mfeldsztejn.ringtest.ui.main

import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.mfeldsztejn.ringtest.Storage
import com.mfeldsztejn.ringtest.data.models.Post
import com.mfeldsztejn.ringtest.data.source.PostsRepository
import com.mfeldsztejn.ringtest.utils.CoroutinesDispatcherExtension
import com.mfeldsztejn.ringtest.utils.InstantExecutorExtension
import com.mfeldsztejn.ringtest.utils.coTest
import com.mfeldsztejn.ringtest.utils.relaxedMockk
import io.mockk.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class, CoroutinesDispatcherExtension::class)
internal class ListViewModelTest {

    private val savedState = spyk<SavedStateHandle>()
    private val repository = relaxedMockk<PostsRepository>()
    private val storage = relaxedMockk<Storage>()

    fun createViewModel() = ListViewModel(storage, repository, savedState)

    @Nested
    @DisplayName("ViewModel#showSubbreddit")
    inner class ShowSubbredditTests {
        private val viewModel by lazy { createViewModel() }

        init {
            every { savedState.set(any(), any<String>()) } just runs
            every { savedState.contains(any()) } returns true
            every { savedState.getLiveData<String>(any()) } returns mockk {
                every { value } returns "android"
            }
        }

        @Test
        fun `with null, does nothing`() {
            viewModel.showSubreddit(null)

            verify(exactly = 0) { savedState.set(any(), any<String>()) }
        }

        @Test
        fun `with same value, does nothing`() {
            viewModel.showSubreddit("android")

            verify(exactly = 0) { savedState.set(any(), any<String>()) }
        }

        @Test
        fun `with new value, sets the saved state`() {
            viewModel.showSubreddit("kotlin")

            verify { savedState.set(any(), "kotlin") }
        }
    }

    @Test
    fun `when creating the viewModel, if no saved state, requests storage for key`() {
        every { storage.getSubreddit() } returns "subreddit"
        every { savedState.contains(any()) } returns false

        createViewModel()

        verify { savedState.set(any(), "subreddit") }
    }

    @Nested
    @DisplayName("CurrentSubreddit LiveData updates")
    inner class CurrentSubredditUpdates {
        private val viewModel by lazy { createViewModel() }

        init {
            // This will avoid the set from the init
            every { savedState.contains(any()) } returns true
        }

        @Test
        fun `saves new subreddit to storage`() {
            val observer = Observer<PagingData<Post>> {}

            viewModel.posts.observeForever(observer)

            viewModel.showSubreddit("android")

            verify { storage.saveSubreddit("android") }
        }

        @Test
        fun `requests new values from repository`() {
            val observer = Observer<PagingData<Post>> {}

            viewModel.posts.observeForever(observer)

            viewModel.showSubreddit("android")

            verify { repository.postsOfSubreddit("android", any()) }
        }
    }

    @Test
    fun `markPostAsRead launches coroutine and delegates to repository`() = coTest {
        val viewModel = createViewModel()

        viewModel.removePost("name")

        coVerify { repository.removePost("name") }
    }

    @Test
    fun `removePost launches coroutine and delegates to repository`() = coTest {
        val viewModel = createViewModel()

        viewModel.removePost("name")

        coVerify { repository.removePost("name") }
    }

    @Nested
    @DisplayName("ViewModel#clearAll")
    inner class ClearAllTests {
        @Test
        fun `with null current subreddit does nothing`() = coTest {
            val viewModel = spyk(createViewModel())
            every { viewModel.currentSubreddit } returns mockk {
                every { value } returns null
            }

            viewModel.clearAll()

            coVerify(exactly = 0) { repository.clearAll(any()) }
        }

        @Test
        fun `with current subreddit delegates to repository`() = coTest {
            val viewModel = spyk(createViewModel())
            every { viewModel.currentSubreddit } returns mockk {
                every { value } returns "android"
            }

            viewModel.clearAll()

            coVerify { repository.clearAll("android") }
        }
    }
}