package com.erank.yogappl.utils

import android.app.Application
import com.erank.yogappl.R
import com.erank.yogappl.utils.data_source.DataSource
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        UnsplashPhotoPicker.init(
            this, // application
            getString(R.string.unsplash_api_key),
            getString(R.string.unsplash_secret)
        )

        DataSource.initRoom(applicationContext)
    }
}