package com.mfeldsztejn.ringtest.utils

import io.mockk.mockk

inline fun <reified T : Any> relaxedMockk(block: T.() -> Unit = {}): T =
    mockk(relaxed = true, block = block)