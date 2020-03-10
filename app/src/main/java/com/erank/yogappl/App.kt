package com.erank.yogappl

import android.app.Application
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        UnsplashPhotoPicker.init(
            this, // application
            getString(R.string.unsplash_api_key),
            getString(R.string.unsplash_secret)
        )
    }
}