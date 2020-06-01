package com.erank.yogappl.data.repository

import android.content.Context
import java.util.*
import javax.inject.Inject


class SharedPrefsHelper @Inject constructor(context: Context) : PreferenceProvider(context) {

    companion object {
        private const val LAST_LOCALE = "last_locale"
        private const val UPDATED_DATE = "moneyLastUpdatedDate"
        private const val MONEY = "money"
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

    fun getString(key: String, defaultValue: String? = null): String? =
        prefs.getString(key, defaultValue)

    fun getLastLocale() = getString(LAST_LOCALE)
    fun putLastLocale(countryCode: String): SharedPrefsHelper {
        put(LAST_LOCALE, countryCode)
        return this
    }


    fun getUpdatedDate() = getLong(UPDATED_DATE)


    fun getMoney() = getFloat(MONEY)
    fun putMoney(localeCurrencyMultiplier: Float): SharedPrefsHelper {
        put(MONEY, localeCurrencyMultiplier)
        return this
    }

    fun putUpdatedDate(): SharedPrefsHelper {
        put(UPDATED_DATE, Date().time)
        return this
    }

}