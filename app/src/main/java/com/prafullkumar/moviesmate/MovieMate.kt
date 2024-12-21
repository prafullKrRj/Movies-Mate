package com.prafullkumar.moviesmate

import android.app.Application
import com.prafullkumar.moviesmate.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MovieMate : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
            androidContext(this@MovieMate)
        }
    }
}