package com.shubhamthorat.echo

import android.app.Application
import com.shubhamthorat.echo.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class EchoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        initKoin {
            androidLogger()
            androidContext(this@EchoApp)
        }
    }
}
