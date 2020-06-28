package com.mfeldsztejn.ringtest.data.source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mfeldsztejn.ringtest.data.models.Post

@Dao
interface PostsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<Post>)

    @Query("SELECT * FROM posts WHERE subreddit = :subreddit")
    fun postsBySubreddit(subreddit: String): PagingSource<Int, Post>

    @Query("DELETE FROM posts WHERE subreddit = :subreddit")
    suspend fun deleteBySubreddit(subreddit: String)

    @Query("UPDATE posts SET isRead = 1 WHERE name = :name")
    suspend fun markPostAsRead(name: String)

    @Query("DELETE FROM posts WHERE name = :name")
    suspend fun removePost(name: String)

    @Query("SELECT * FROM posts WHERE name = :name")
    suspend fun postByName(name: String): Post
}
