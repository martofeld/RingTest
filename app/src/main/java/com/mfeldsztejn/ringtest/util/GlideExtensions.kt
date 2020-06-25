package com.mfeldsztejn.ringtest.util

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mfeldsztejn.ringtest.GlideRequest

inline fun <T> GlideRequest<T>.doOnFinish(crossinline callback: (T?) -> Unit): GlideRequest<T> {
    return listener(object : RequestListener<T> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<T>?,
            isFirstResource: Boolean
        ): Boolean {
            callback(null)
            return false
        }

        override fun onResourceReady(
            resource: T?,
            model: Any?,
            target: Target<T>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            callback(resource)
            return false
        }

    })
}