package com.erank.yogappl.utils

import android.app.Application
import android.util.Log
import com.erank.yogappl.R
import com.erank.yogappl.data.injection.AppComponent
import com.erank.yogappl.data.injection.DaggerAppComponent
import com.erank.yogappl.data.room.AppDatabase
import com.erank.yogappl.utils.helpers.NotificationsHelper
import com.facebook.stetho.Stetho
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker
import javax.inject.Inject

class App : Application() {
    @Inject
    lateinit var appDB: AppDatabase

    @Inject
    lateinit var notificationsHelper: NotificationsHelper

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

        notificationsHelper.createDefaultChannel()
    }

    fun getAppComponent(): AppComponent = appComponent

}