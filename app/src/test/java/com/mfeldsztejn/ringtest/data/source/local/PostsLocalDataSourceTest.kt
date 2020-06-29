package com.mfeldsztejn.ringtest.data.source.local

import androidx.room.R
import androidx.room.withTransaction
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.mfeldsztejn.ringtest.data.models.PostDTO
import com.mfeldsztejn.ringtest.data.models.PostDataDTO
import com.mfeldsztejn.ringtest.data.models.entities.SubredditRemoteKey
import com.mfeldsztejn.ringtest.utils.*
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostsLocalDataSourceTest {

    @[JvmField Rule]
    val instantExecutorRule = RuleOfExtension(InstantExecutorExtension())

    @[JvmField Rule]
    val coroutinesRule = RuleOfExtension(CoroutinesDispatcherExtension())

    private val postsDao = relaxedMockk<PostsDao>()
    private val remoteKeyDao = relaxedMockk<RemoteKeyDao>()

    private val database = mockk<PostsDatabase> {
        every { postDao } returns postsDao
        every { keyDao } returns remoteKeyDao
    }

    @Before
    fun setUp() {
        mockkStatic("androidx.room.RoomDatabaseKt")

        val transactionLambda = slot<suspend () -> R>()
        coEvery { database.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }
    }

    @After
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
        val newItems = listOf<PostDTO<PostDataDTO>>(mockk(), mockk(), mockk()) // Insert 3 posts

        localDataSource.updateBySubreddit("android", newItems, "after")

        with(capturedKey.captured) {
            assertThat(nextPageKey).isEqualTo("after")
            assertThat(subreddit).isEqualTo("android")
        }
        coVerify {
            postsDao.insertAll(any())
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
            val newItems = listOf<PostDTO<PostDataDTO>>(relaxedMockk{
                every { data } returns relaxedMockk()
            }, relaxedMockk{
                every { data } returns relaxedMockk()
            }, relaxedMockk{
                every { data } returns relaxedMockk()
            }) // Insert 3 posts

            localDataSource.updateBySubreddit("android", newItems, "after")

            with(capturedKey.captured) {
                assertThat(nextPageKey).isEqualTo("after")
                assertThat(subreddit).isEqualTo("android")
            }
            coVerifyOrder {
                remoteKeyDao.insert(capturedKey.captured)
                postsDao.insertAll(any())
            }
        }

    @Test
    fun `when marking post as read, executes update in database`() = coTest {
        localDataSource.markPostAsRead("name")
        coVerify { localDataSource.markPostAsRead("name") }
    }
}