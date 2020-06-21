package com.erank.yogappl.utils

import android.app.Application
import android.util.Log
import com.erank.yogappl.R
import com.erank.yogappl.data.injection.AppComponent
import com.erank.yogappl.data.injection.DaggerAppComponent
import com.erank.yogappl.data.room.AppDatabase
import com.facebook.stetho.Stetho
import com.google.android.gms.ads.MobileAds
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker
import javax.inject.Inject

class App : Application() {
    @Inject
    lateinit var appDB: AppDatabase

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        UnsplashPhotoPicker.init(
            this, // application
            getString(R.string.unsplash_api_key),
            getString(R.string.unsplash_secret)
        )

        Stetho.initializeWithDefaults(this)

        appComponent = DaggerAppComponent.factory().create(this)

        appComponent.inject(this)
        runOnBackground({appDB.clearAllTables()}){
            Log.d("App", "onTerminate: cleared tables")
        }

        MobileAds.initialize(this)
    }

    fun getAppComponent(): AppComponent = appComponent

}