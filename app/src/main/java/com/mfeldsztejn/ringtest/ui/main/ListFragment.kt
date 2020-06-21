package com.mfeldsztejn.ringtest.ui.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mfeldsztejn.ringtest.GlideApp
import com.mfeldsztejn.ringtest.R
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class ListFragment : Fragment(R.layout.main_fragment) {


    companion object {
        fun newInstance() = ListFragment()
    }

    private val adapter by lazy { PostsAdapter(GlideApp.with(this)) }
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
}