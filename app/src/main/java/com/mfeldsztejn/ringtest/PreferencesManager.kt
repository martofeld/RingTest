package com.mfeldsztejn.ringtest

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

interface Storage {
    fun saveSubreddit(subreddit: String)
    fun getSubreddit(): String
}

class PreferencesManager(context: Context): Storage {

    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    override fun saveSubreddit(subreddit: String) =
        sharedPrefs.edit { putString(SUBREDDIT_KEY, subreddit) }

    override fun getSubreddit(): String =
        sharedPrefs.getString(SUBREDDIT_KEY, null) ?: "kotlin"

    companion object {
        private const val SUBREDDIT_KEY = "subreddit"
    }
}