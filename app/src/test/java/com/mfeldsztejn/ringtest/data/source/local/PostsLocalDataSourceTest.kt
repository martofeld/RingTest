package com.mfeldsztejn.ringtest.data.source.local

import androidx.room.R
import androidx.room.withTransaction
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.mfeldsztejn.ringtest.data.models.Post
import com.mfeldsztejn.ringtest.data.models.entities.SubredditRemoteKey
import com.mfeldsztejn.ringtest.utils.CoroutinesDispatcherExtension
import com.mfeldsztejn.ringtest.utils.InstantExecutorExtension
import com.mfeldsztejn.ringtest.utils.coTest
import com.mfeldsztejn.ringtest.utils.relaxedMockk
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class, CoroutinesDispatcherExtension::class)
class PostsLocalDataSourceTest {

    private val postsDao = relaxedMockk<PostsDao>()
    private val remoteKeyDao = relaxedMockk<RemoteKeyDao>()

    private val database = mockk<PostsDatabase> {
        every { postDao } returns postsDao
        every { keyDao } returns remoteKeyDao
    }

    @BeforeEach
    fun setUp() {
        mockkStatic("androidx.room.RoomDatabaseKt")

        val transactionLambda = slot<suspend () -> R>()
        coEvery { database.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic("androidx.room.RoomDatabaseKt")
    }

    private val localDataSource = PostsLocalDataSource(database)

    @Test
    fun `when requesting the posts, it is delegated to database`() = coTest {
        localDataSource.postsBySubreddit("android")

        coVerify { postsDao.postsBySubreddit("android") }
    }

    @Test
    fun `when adding new posts, no refresh, it adds the key and the new posts`() = coTest {
        val capturedKey = slot<SubredditRemoteKey>()
        coEvery { remoteKeyDao.insert(capture(capturedKey)) } just runs
        val newItems = listOf<Post>(mockk(), mockk(), mockk()) // Insert 3 posts

        localDataSource.updateBySubreddit("android", newItems, "after", false)

        with(capturedKey.captured) {
            assertThat(nextPageKey).isEqualTo("after")
            assertThat(subreddit).isEqualTo("android")
        }
        coVerify {
            postsDao.insertAll(newItems)
            remoteKeyDao.insert(capturedKey.captured)
        }
        coVerify(exactly = 0) {
            postsDao.deleteBySubreddit(any())
            remoteKeyDao.deleteBySubreddit(any())
        }
    }

    @Test
    fun `when adding new posts, with refresh, it clears the current data and adds the new key and the new posts`() =
        coTest {
            val capturedKey = slot<SubredditRemoteKey>()
            coEvery { remoteKeyDao.insert(capture(capturedKey)) } just runs
            val newItems = listOf<Post>(mockk(), mockk(), mockk()) // Insert 3 posts

            localDataSource.updateBySubreddit("android", newItems, "after", true)

            with(capturedKey.captured) {
                assertThat(nextPageKey).isEqualTo("after")
                assertThat(subreddit).isEqualTo("android")
            }
            coVerifyOrder {
                postsDao.deleteBySubreddit("android")
                remoteKeyDao.deleteBySubreddit("android")
                remoteKeyDao.insert(capturedKey.captured)
                postsDao.insertAll(any())
            }
        }

    @Test
    fun `when marking post as read, executes update in database`() = coTest {
        localDataSource.markPostAsRead(1)
        coVerify { localDataSource.markPostAsRead(1) }
    }
}