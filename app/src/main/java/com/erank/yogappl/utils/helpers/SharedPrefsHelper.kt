package com.erank.yogappl.utils.helpers

import android.content.Context
import javax.inject.Inject


class SharedPrefsHelper @Inject constructor(context: Context) : PreferenceProvider(context) {

    companion object {
        const val IS_FIRST_TIME = "IS_FIRST_TIME"
    }

    fun put(key: String, data: Long) {
        prefs.edit().putLong(key, data).apply()
    }

    fun put(key: String, data: Float) {
        prefs.edit().putFloat(key, data).apply()
    }

    fun put(key: String, data: String) {
        prefs.edit().putString(key, data).apply()
    }

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun getLong(key: String) =
        if (!prefs.contains(key)) null
        else prefs.getLong(key, 0)

    fun getInt(key: String, defaultValue: Int) =
        prefs.getInt(key, defaultValue)

    fun getFloat(key: String) =
        if (!prefs.contains(key)) null
        else prefs.getFloat(key, 0f)

    fun getString(key: String, defaultValue: String?): String? =
        prefs.getString(key, defaultValue)

}