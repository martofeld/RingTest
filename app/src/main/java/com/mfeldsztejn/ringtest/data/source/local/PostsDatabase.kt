package com.mfeldsztejn.ringtest.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mfeldsztejn.ringtest.data.models.Post
import com.mfeldsztejn.ringtest.data.models.entities.SubredditRemoteKey

@Database(version = 4, entities = [Post::class, SubredditRemoteKey::class])
abstract class PostsDatabase : RoomDatabase() {
    abstract val keyDao: RemoteKeyDao
    abstract val postDao: PostsDao
}