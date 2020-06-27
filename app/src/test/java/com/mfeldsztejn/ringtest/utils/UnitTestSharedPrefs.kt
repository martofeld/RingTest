package com.mfeldsztejn.ringtest.utils

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every

fun unitTestSharedPrefs(sharedPreferencesMap: MutableMap<String, Any> = mutableMapOf()) =
    relaxedMockk<SharedPreferences> {
        every { edit() } returns relaxedMockk {
            every { putString(any(), any()) } answers {
                val key = this.args[0] as String
                val value = this.args[1] as String
                sharedPreferencesMap[key] = value
                self as SharedPreferences.Editor
            }
        }
        every { getString(any(), any()) } answers {
            sharedPreferencesMap[args[0] as String] as String?
        }
    }

fun unitTestContext(sharedPreferencesMap: MutableMap<String, Any> = mutableMapOf()) =
    relaxedMockk<Context> {
        every { getSharedPreferences(any(), any()) } returns unitTestSharedPrefs(
            sharedPreferencesMap
        )
    }