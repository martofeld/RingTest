package com.mfeldsztejn.ringtest.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun ViewGroup.inflate(@LayoutRes layout: Int, attach: Boolean = false) =
    LayoutInflater.from(context).inflate(layout, this, attach)

fun ViewGroup.snackbar(
    @StringRes message: Int,
    @StringRes actionMessage: Int,
    action: (View) -> Unit
) =
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .setAction(actionMessage, action)
        .show()