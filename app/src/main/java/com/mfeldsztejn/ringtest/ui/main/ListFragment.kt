package com.mfeldsztejn.ringtest.ui.main

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mfeldsztejn.ringtest.GlideApp
import com.mfeldsztejn.ringtest.R
import com.mfeldsztejn.ringtest.util.snackbar
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class ListFragment : Fragment(R.layout.main_fragment), Listener {

    interface Listener {
        fun onDetailSelected(
            postId: Int,
            sharedElements: Array<out View>
        )
    }

    companion object {
        fun newInstance() = ListFragment()
    }

    private val adapter by lazy { PostsAdapter(GlideApp.with(this), this) }
    private val viewModel by stateViewModel<ListViewModel>()
    private var interactionListener: Listener? = null
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        postponeEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
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
        lifecycleScope.launch {
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .collectLatest {
                    if (it.refresh is LoadState.Error) {
                        list_fragment_root.snackbar(R.string.generic_error, R.string.retry) {
                            adapter.retry()
                        }
                    }
                    swipe_refresh.isRefreshing = it.refresh is LoadState.Loading
                }
        }

        viewModel.posts.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }

        viewModel.currentSubrredit.observe(viewLifecycleOwner) {
            toolbar.title = it
        }

        swipe_refresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        interactionListener = context as? Listener
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.showSubreddit(query)
                return true
            }

            override fun onQueryTextChange(newText: String?) = false
        })
        searchView.setOnSearchClickListener {
            searchView.setQuery(viewModel.currentSubrredit.value, false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.clear_all) {
            viewModel.clearAll()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDismiss(id: Int) {
        viewModel.removePost(id)
    }

    override fun onOpen(id: Int, vararg sharedElements: View) {
        viewModel.markPostAsRead(id)
        interactionListener?.onDetailSelected(id, sharedElements)
    }
}

class Animator : SlideInLeftAnimator() {

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