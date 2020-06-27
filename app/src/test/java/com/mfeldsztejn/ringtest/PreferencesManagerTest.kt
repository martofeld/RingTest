package com.mfeldsztejn.ringtest

import android.content.Context
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.mfeldsztejn.ringtest.utils.relaxedMockk
import com.mfeldsztejn.ringtest.utils.unitTestContext
import com.mfeldsztejn.ringtest.utils.unitTestSharedPrefs
import io.mockk.every
import org.junit.jupiter.api.Test

internal class PreferencesManagerTest {

    private val sharedPreferencesMap = mutableMapOf<String, Any>()

    private val context = unitTestContext(sharedPreferencesMap)
    private val sharedPreferencesManager = PreferencesManager(context)

    @Test
    fun `test save current subreddit`() {
        assertThat(sharedPreferencesMap).isEmpty()
        sharedPreferencesManager.saveSubreddit("android")

        assertThat(sharedPreferencesMap.containsValue("android")).isTrue()
    }

    @Test
    fun `test get current subreddit`() {
        assertThat(sharedPreferencesMap).isEmpty()
        sharedPreferencesManager.saveSubreddit("marvel")

        assertThat(sharedPreferencesManager.getSubreddit()).isEqualTo("marvel")
    }

    @Test
    fun `test default value for subreddit is kotlin`() {
        assertThat(sharedPreferencesMap).isEmpty()

        assertThat(sharedPreferencesManager.getSubreddit()).isEqualTo("kotlin")
    }
}