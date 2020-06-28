package com.mfeldsztejn.ringtest.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewMatcher(val recyclerViewId: Int) {
    fun atPosition(position: Int, targetViewId: Int = -1): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            private var childView: View? = null

            override fun describeTo(description: Description?) {
                description?.appendText("With id: ${childView?.resources?.getResourceName(recyclerViewId)}")
            }

            override fun matchesSafely(item: View?): Boolean {
                if(childView == null) {
                    item?.rootView
                        ?.findViewById<RecyclerView>(recyclerViewId)
                        ?.let { childView = it.findViewHolderForAdapterPosition(position)?.itemView } ?: return false
                }
                return if(targetViewId == -1) {
                    item == childView
                } else {
                    val view = childView?.findViewById<View>(targetViewId)
                    item == view
                }
            }

        }
    }
}