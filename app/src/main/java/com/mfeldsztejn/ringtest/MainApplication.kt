package com.mfeldsztejn.ringtest

import android.app.Application
import com.mfeldsztejn.ringtest.di.*
import com.mfeldsztejn.ringtest.util.NotificationManager

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin(this, networkModule, databaseModule, repositoriesModule, viewModelModule)
        NotificationManager.registerChannel(this)
    }
}