package com.mfeldsztejn.ringtest

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import com.mfeldsztejn.ringtest.ui.detail.DetailFragment
import com.mfeldsztejn.ringtest.ui.main.ListFragment
import kotlinx.android.synthetic.main.main_activity.*

private const val LIST_FRAGMENT_TAG = "list"
private const val DETAIL_FRAGMENT_TAG = "detail"
private const val IS_SHOWING_DETAIL_KEY = "is_showing_detail"

class MainActivity : AppCompatActivity(), ListFragment.Listener {

    private var isShowingDetail = false
    private val isTwoPane: Boolean
        get() = detail_fragment != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                replaceListFragment()
            }
        } else {
            isShowingDetail = savedInstanceState.getBoolean(IS_SHOWING_DETAIL_KEY)
            if (isShowingDetail) {
                val detailFragment = supportFragmentManager[DETAIL_FRAGMENT_TAG] ?: return
                if (isTwoPane) {
                    val listFragment = supportFragmentManager[LIST_FRAGMENT_TAG] ?: return
                    supportFragmentManager.commitNow {
                        replaceListFragment(listFragment)
                        replaceDetailFragment(R.id.detail_fragment, detailFragment)
                    }
                } else {
                    supportFragmentManager.commitNow {
                        replaceDetailFragment(R.id.main_fragment, detailFragment)
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_SHOWING_DETAIL_KEY, isShowingDetail)
    }

    override fun onBackPressed() {
        if (isShowingDetail) {
            isShowingDetail = false
            if (isTwoPane || supportFragmentManager.popBackStackImmediate()) {
                val detailFragment = supportFragmentManager[DETAIL_FRAGMENT_TAG] ?: return
                supportFragmentManager.commitNow {
                    remove(detailFragment)
                }
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onDetailSelected(postId: Int, sharedElements: Array<out View>) {
        isShowingDetail = true
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            val newFragment = DetailFragment.newInstance(postId)
            if (isTwoPane) {
                replaceDetailFragment(R.id.detail_fragment, newFragment = newFragment)
            } else {
                addToBackStack(null)
                sharedElements
                    .filterNot { it.transitionName.isNullOrEmpty() }
                    .forEach { addSharedElement(it, it.transitionName) }
                replaceDetailFragment(R.id.main_fragment, newFragment = newFragment)
            }
        }
    }
}

/**
 * Utility method to ensure I always set the tag
 */
private fun FragmentTransaction.replaceListFragment(fragmentInstance: Fragment = ListFragment.newInstance()) {
    replace(R.id.main_fragment, fragmentInstance, LIST_FRAGMENT_TAG)
}

/**
 * Utility method to ensure I always set the tag
 */
private fun FragmentTransaction.replaceDetailFragment(
    container: Int,
    originalDetailFragment: Fragment? = null,
    newFragment: DetailFragment = DetailFragment().apply {
        arguments = originalDetailFragment?.arguments
    }
) {
    setCustomAnimations(
        R.anim.slide_in_right,
        R.anim.slide_out_left,
        android.R.anim.slide_in_left,
        android.R.anim.slide_out_right
    )
    originalDetailFragment?.let { remove(it) }
    replace(
        container,
        newFragment,
        DETAIL_FRAGMENT_TAG
    )
}

private operator fun FragmentManager.get(tag: String) = findFragmentByTag(tag)