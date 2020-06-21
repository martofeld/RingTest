package com.mfeldsztejn.ringtest

import android.app.Application
import com.mfeldsztejn.ringtest.di.initializeKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin(this)
    }
}