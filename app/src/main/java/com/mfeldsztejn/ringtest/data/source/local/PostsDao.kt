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

    @Query("SELECT * FROM posts WHERE subreddit = :subreddit ORDER BY indexInResponse ASC")
    fun postsBySubreddit(subreddit: String): PagingSource<Int, Post>

    @Query("DELETE FROM posts WHERE subreddit = :subreddit")
    suspend fun deleteBySubreddit(subreddit: String)

    @Query("UPDATE posts SET isRead = 1 WHERE id = :id")
    suspend fun markPostAsRead(id: Int)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun removePost(id: Int)
}
