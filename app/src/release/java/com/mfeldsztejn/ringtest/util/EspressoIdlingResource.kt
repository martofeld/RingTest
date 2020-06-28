package com.mfeldsztejn.ringtest.util

/**
 * The debug version actually does things, but we don't want espresso in prod
 * and since its an inline fun there will be absolutely no traces of this in the final apk
 */
inline fun <T> wrapEspressoIdlingResource(function: () -> T): T = function()