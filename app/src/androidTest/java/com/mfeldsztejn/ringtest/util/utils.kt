package com.mfeldsztejn.ringtest.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mfeldsztejn.ringtest.data.models.PostDataDTO
import com.mfeldsztejn.ringtest.data.models.PostsResponseDTO
import java.io.InputStreamReader
import java.io.Reader

fun loadResponseForSubreddit(context: Context, subreddit: String): PostsResponseDTO<PostDataDTO> {
    return context.assets.open("${subreddit}_response.json").use {
        Gson().fromJson<PostsResponseDTO<PostDataDTO>>(InputStreamReader(it))
    }
}

inline fun <reified T> Gson.fromJson(json: Reader) =
    fromJson<T>(json, object : TypeToken<T>() {}.type)