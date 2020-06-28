package com.mfeldsztejn.ringtest.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest

@OptIn(ExperimentalCoroutinesApi::class)
fun coTest(block: suspend TestCoroutineScope.() -> Unit) = runBlockingTest(testBody = block)