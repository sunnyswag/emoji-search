package com.example.emojisemanticsearch

import android.app.Application
import com.example.emojisemanticsearch.network.networkModule
import com.example.emojisemanticsearch.network.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(networkModule, repositoryModule, viewModelModule)
        }
    }
}