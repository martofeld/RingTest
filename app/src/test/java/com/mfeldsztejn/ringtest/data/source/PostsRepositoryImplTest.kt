package com.mfeldsztejn.ringtest.data.source

import com.mfeldsztejn.ringtest.data.source.local.PostsLocalDataSource
import com.mfeldsztejn.ringtest.data.source.remote.PostsRemoteDataSource
import com.mfeldsztejn.ringtest.utils.CoroutinesDispatcherExtension
import com.mfeldsztejn.ringtest.utils.InstantExecutorExtension
import com.mfeldsztejn.ringtest.utils.coTest
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class, CoroutinesDispatcherExtension::class)
class PostsRepositoryImplTest {
    private val remoteDataSource = mockk<PostsRemoteDataSource>()
    private val localDataSource = mockk<PostsLocalDataSource>()

    private val repository = PostsRepositoryImpl(localDataSource, remoteDataSource)

    @Test
    fun `when repository is requested to remove post, it is delegated to local data source`() =
        coTest {
            coEvery { localDataSource.removePost(any()) } answers { Unit }
            repository.removePost(1)

            coVerify { localDataSource.removePost(1) }
            verify { remoteDataSource wasNot called }
        }

    @Test
    fun `when repository is requested to mark post as read, it is delegated to local data source`() =
        coTest {
            coEvery { localDataSource.markPostAsRead(any()) } answers { Unit }
            repository.markPostAsRead(1)

            coVerify { localDataSource.markPostAsRead(1) }
            verify { remoteDataSource wasNot called }
        }

    @Test
    fun `when repository is requested a post, it is delegated to local data source`() =
        coTest {
            coEvery { localDataSource.getPostById(any()) } answers { mockk() }
            repository.getPostById(1)

            coVerify { localDataSource.getPostById(1) }
            verify { remoteDataSource wasNot called }
        }
}