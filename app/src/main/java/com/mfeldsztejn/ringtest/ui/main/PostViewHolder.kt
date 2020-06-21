/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mfeldsztejn.ringtest.ui.main

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mfeldsztejn.ringtest.GlideRequests
import com.mfeldsztejn.ringtest.R
import com.mfeldsztejn.ringtest.data.models.Post
import com.mfeldsztejn.ringtest.util.inflate
import kotlinx.android.synthetic.main.post_view_holder.view.*

/**
 * A RecyclerView ViewHolder that displays a reddit post.
 */
class PostViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(parent.inflate(R.layout.post_view_holder)) {

    fun bind(post: Post?, glide: GlideRequests) {
        with(itemView) {
            author.text = post?.author ?: "loading"
            title.text = post?.title ?: "loading"
            if (post?.thumbnail?.startsWith("http") == true) {
                thumbnail.visibility = View.VISIBLE
                glide.load(post.thumbnail)
                    .centerCrop()
                    .placeholder(R.drawable.ic_clear)
                    .into(thumbnail)
            } else {
                thumbnail.visibility = View.GONE
                glide.clear(thumbnail)
            }
            updateComments(post)
            setOnClickListener {

            }
        }
    }

    fun updateComments(item: Post?) {
        itemView.comments.text = "${item?.comments ?: 0} comments"
    }
}