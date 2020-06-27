package com.mfeldsztejn.ringtest.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest

inline fun <reified T : Any> relaxedMockk(block: T.() -> Unit = {}): T =
    mockk(relaxed = true, block = block)

@OptIn(ExperimentalCoroutinesApi::class)
fun coTest(block: suspend TestCoroutineScope.() -> Unit) = runBlockingTest(testBody = block)

fun assertTrue(block: () -> Boolean) = assertThat(block).isEqualTo(Result.success(true))