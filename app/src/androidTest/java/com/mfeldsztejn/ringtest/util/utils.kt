package com.mfeldsztejn.ringtest.util

import android.content.Context
import com.google.gson.Gson
import com.mfeldsztejn.ringtest.data.models.PostsResponseDTO
import java.io.InputStreamReader

fun loadResponseForSubreddit(context: Context, subreddit: String): PostsResponseDTO {
    return context.assets.open("${subreddit}_response.json").use {
        Gson().fromJson(InputStreamReader(it), PostsResponseDTO::class.java) as PostsResponseDTO
    }
}