package com.mfeldsztejn.ringtest.ui.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mfeldsztejn.ringtest.GlideApp
import com.mfeldsztejn.ringtest.R
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class ListFragment : Fragment(R.layout.main_fragment), Listener {


    companion object {
        fun newInstance() = ListFragment()
    }

    private val adapter by lazy { PostsAdapter(GlideApp.with(this), this) }
    private val viewModel by stateViewModel<ListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostsLoadStateAdapter(adapter),
            footer = PostsLoadStateAdapter(adapter)
        )
        list.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        list.itemAnimator = Animator()
        initSearch()
        adapter.addLoadStateListener {
            swipe_refresh.isRefreshing = it.refresh is LoadState.Loading
        }

        viewModel.posts.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }

        swipe_refresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun initSearch() {
        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updatedSubredditFromInput()
                true
            } else {
                false
            }
        }
        input.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updatedSubredditFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updatedSubredditFromInput() {
        input.text.trim().toString().let {
            if (it.isNotBlank()) {
                viewModel.showSubreddit(it)
            }
        }
    }

    override fun onDismiss(id: Int) {
        viewModel.removePost(id)
    }

    override fun onOpen(id: Int) {
        viewModel.markPostAsRead(id)
    }
}

class Animator: SlideInLeftAnimator() {

    override fun preAnimateAddImpl(holder: RecyclerView.ViewHolder?) {
        holder?.itemView?.let {
            it.translationX = it.rootView.width.toFloat()
        }
    }

    override fun animateAddImpl(holder: RecyclerView.ViewHolder) {
        ViewCompat.animate(holder.itemView)
            .translationX(0f)
            .setDuration(addDuration)
            .setInterpolator(mInterpolator)
            .setListener(DefaultAddVpaListener(holder))
            .setStartDelay(getAddDelay(holder))
            .start()
    }
}