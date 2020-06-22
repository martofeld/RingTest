package com.mfeldsztejn.ringtest.ui.main

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.mfeldsztejn.ringtest.GlideRequests
import com.mfeldsztejn.ringtest.data.models.Post

interface Listener {
    fun onDismiss(id: Int)
    fun onOpen(id: Int, vararg sharedElements: Pair<String?, View>)
}

class PostsAdapter(private val glide: GlideRequests, private val listener: Listener) :
    PagingDataAdapter<Post, PostViewHolder>(POST_COMPARATOR) {

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), glide, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(parent)
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Post>() {
            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem.id == newItem.id
        }
    }
}
