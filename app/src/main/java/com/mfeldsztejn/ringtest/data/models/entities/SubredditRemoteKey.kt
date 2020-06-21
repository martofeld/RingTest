package com.mfeldsztejn.ringtest.data.models.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class SubredditRemoteKey(
    @PrimaryKey
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val subreddit: String, // technically mutable but fine for a demo
    val nextPageKey: String?
)