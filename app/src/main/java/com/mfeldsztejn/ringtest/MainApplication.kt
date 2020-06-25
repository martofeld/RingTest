package com.mfeldsztejn.ringtest

import android.app.Application
import com.mfeldsztejn.ringtest.di.initializeKoin
import com.mfeldsztejn.ringtest.util.NotificationManager

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin(this)
        NotificationManager.registerChannel(this)
    }
}