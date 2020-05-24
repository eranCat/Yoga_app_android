package com.erank.yogappl.utils.helpers

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

abstract class PreferenceProvider(context: Context) {
    private val appContext = context.applicationContext

    protected val prefs: SharedPreferences
        get() = appContext.getSharedPreferences("user", MODE_PRIVATE)


}