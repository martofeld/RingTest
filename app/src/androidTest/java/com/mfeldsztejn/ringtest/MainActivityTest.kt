package com.mfeldsztejn.ringtest


import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.mfeldsztejn.ringtest.data.source.local.PostsDatabase
import com.mfeldsztejn.ringtest.data.source.remote.RedditAPI
import com.mfeldsztejn.ringtest.ui.main.PostViewHolder
import com.mfeldsztejn.ringtest.util.EspressoIdlingResource
import com.mfeldsztejn.ringtest.util.RecyclerViewMatcher
import com.mfeldsztejn.ringtest.util.loadResponseForSubreddit
import io.mockk.coEvery
import io.mockk.mockkClass
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declare
import org.koin.test.mock.declareMock

@ExperimentalStdlibApi
@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest : KoinTest {

    @[Rule JvmField]
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz.java.kotlin)
    }

    @Before
    fun setUp() {
        declare {
            Room
                .inMemoryDatabaseBuilder(
                    InstrumentationRegistry.getInstrumentation().context,
                    PostsDatabase::class.java
                )
                .build()
        }
        declareMock<RedditAPI> {
            coEvery { getPosts(any(), any(), any(), any()) } coAnswers {
                val subreddit = args[0] as String
                val limit = args[3] as Int
                val response = loadResponseForSubreddit(
                    InstrumentationRegistry.getInstrumentation().context,
                    subreddit
                )
                val children = response.data.children
                val finalChildren = buildList {
                    (0 until limit).forEach {
                        addAll(children)
                    }
                }
                response.copy(data = response.data.copy(children = finalChildren))
            }
        }
    }

    @After
    fun tearDown() {
        InstrumentationRegistry.getInstrumentation().targetContext.run {
            PreferenceManager.getDefaultSharedPreferences(this).edit(commit = true) { clear() }
        }
    }

    @Test
    fun whenOpeningAnElement_itIsMarkedAsRead() {
        val scenario = withIdlingResource(EspressoIdlingResource.countingIdlingResource) {
            ActivityScenario.launch(MainActivity::class.java)
        }

        scenario.use {
            val recyclerView = onView(withId(R.id.list))
            withIdlingResource(EspressoIdlingResource.countingIdlingResource) {
                recyclerView.perform(
                    RecyclerViewActions.actionOnItemAtPosition<PostViewHolder>(
                        0,
                        click()
                    )
                )
            }

            onView(isRoot()).perform(pressBack())

            onView(
                RecyclerViewMatcher(R.id.list)
                    .atPosition(0, R.id.unread_indicator)
            )
                .check(matches(not(isDisplayed())))
        }
    }

}

inline fun <T> withIdlingResource(resource: IdlingResource, block: () -> T): T {
    IdlingRegistry.getInstance().register(resource)
    try {
        return block()
    } finally {
        IdlingRegistry.getInstance().register(resource)
    }
}