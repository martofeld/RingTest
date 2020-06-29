package com.mfeldsztejn.ringtest.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
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
    @PrimaryKey
    val name: String,
    val title: String,
    val comments: Int,
    val createdUtc: Long,
    val stickied: Boolean,
    val isRead: Boolean = false
) {
    var indexInResponse: Int = -1
}

data class Image(val url: String, val width: Int, val height: Int) : Serializable

data class Detail(
    val text: String? = null,
    val url: String? = null,
    val image: Image? = null
)