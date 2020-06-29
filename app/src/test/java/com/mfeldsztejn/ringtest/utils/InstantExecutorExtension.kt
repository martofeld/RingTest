package com.mfeldsztejn.ringtest.utils

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class InstantExecutorExtension : BeforeEachCallback, AfterEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()
            override fun postToMainThread(runnable: Runnable) = runnable.run()
            override fun isMainThread(): Boolean = true
        })
    }

    override fun afterEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }
}

class RuleOfExtension<T>(val extension: T) :
    TestRule where T : BeforeEachCallback, T : AfterEachCallback {
    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    extension.beforeEach(null)
                    base?.evaluate()
                } finally {
                    extension.afterEach(null)
                }
            }

        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinesDispatcherExtension : BeforeEachCallback, AfterEachCallback {
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }

}