package com.arctouch.codechallenge.core

import android.app.Application
import com.arctouch.codechallenge.di.apiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(apiModule)
        }
    }

}