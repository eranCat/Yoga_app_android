package com.erank.yogappl.utils

import android.app.Application
import com.erank.yogappl.R
import com.erank.yogappl.data.injection.AppComponent
import com.erank.yogappl.data.injection.DaggerAppComponent
import com.erank.yogappl.data.room.AppDatabase
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker
import javax.inject.Inject

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

    @Inject
    lateinit var appDB: AppDatabase

    override fun onTerminate() {
        appComponent.inject(this)
        runOnBackground({appDB.clearAllTables()})
        super.onTerminate()
    }
}