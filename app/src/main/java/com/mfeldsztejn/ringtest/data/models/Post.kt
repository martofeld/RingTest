package com.mfeldsztejn.ringtest.data.models

import androidx.room.*
import java.io.Serializable

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
    val text: String?,
    val url: String,
    val stickied: Boolean,
    @Embedded(prefix = "image_") val image: Image?,
    val isRead: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) {
    var indexInResponse: Int = -1
}

data class Image(val url: String, val width: Int, val height: Int) : Serializable