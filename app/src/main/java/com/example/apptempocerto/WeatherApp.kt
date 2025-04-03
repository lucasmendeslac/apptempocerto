package com.example.apptempocerto

import android.app.Application
import com.example.apptempocerto.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WeatherApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@WeatherApp)
            modules(appModule)
        }
    }
} 