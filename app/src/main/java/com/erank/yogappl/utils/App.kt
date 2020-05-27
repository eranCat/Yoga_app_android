package com.erank.yogappl.utils

import android.app.Application
import com.erank.yogappl.R
import com.erank.yogappl.data.injection.AppComponent
import com.erank.yogappl.data.injection.DaggerAppComponent
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker

class App : Application() {
    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        UnsplashPhotoPicker.init(
            this, // application
            getString(R.string.unsplash_api_key),
            getString(R.string.unsplash_secret)
        )

        appComponent = DaggerAppComponent.factory().create(this)
    }

    fun getAppComponent(): AppComponent = appComponent
}