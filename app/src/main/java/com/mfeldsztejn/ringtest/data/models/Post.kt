package com.mfeldsztejn.ringtest.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "posts",
    indices = [Index(value = ["subreddit"], unique = false)]
)
data class Post(
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val subreddit: String,
    val domain: String,
    val thumbnail: String?,
    val author: String,
    val name: String,
    val title: String,
    val comments: Int,
    val createdUtc: Long,
    val isRead: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) {
    var indexInResponse: Int = -1
}