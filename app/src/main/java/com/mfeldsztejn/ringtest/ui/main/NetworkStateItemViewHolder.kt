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

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.recyclerview.widget.RecyclerView
import com.mfeldsztejn.ringtest.R
import com.mfeldsztejn.ringtest.util.inflate
import kotlinx.android.synthetic.main.network_state_item.view.*

class NetworkStateItemViewHolder(
    parent: ViewGroup,
    private val retryCallback: () -> Unit
) : RecyclerView.ViewHolder(
    parent.inflate(R.layout.network_state_item)
) {
    fun bindTo(loadState: LoadState) {
        itemView.progress_bar.isVisible = loadState is Loading
        itemView.retry_button.isVisible = loadState is Error
        itemView.retry_button.setOnClickListener {
            retryCallback()
        }
        itemView.error_msg.isVisible = !(loadState as? Error)?.error?.message.isNullOrBlank()
        itemView.error_msg.text = (loadState as? Error)?.error?.message
    }
}
