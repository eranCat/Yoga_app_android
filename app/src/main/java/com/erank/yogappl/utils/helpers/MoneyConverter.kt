package com.erank.yogappl.utils.helpers

import com.erank.yogappl.data.network.CurrencyApi
import com.erank.yogappl.data.repository.SharedPrefsHelper
import java.util.*

class MoneyConverter(
    val api: CurrencyApi,
    val prefs: SharedPrefsHelper,
    val locationHelper: LocationHelper
) {

    private val defaultCode = Currency.getInstance(Locale.US).currencyCode

    suspend fun convertFromLocaleToDefault(amount: Number) =
        convert(amount, getLocalCode(), defaultCode)

    suspend fun convertFromDefaultToLocale(amount: Number) =
        convert(amount, defaultCode, getLocalCode())

    private fun getLocalCode(): String {
        val countryCode = prefs.getLastLocale()!!
        val locale = Locale("EN", countryCode)
        return Currency.getInstance(locale).currencyCode
    }

    private suspend fun convert(amount: Number, from: String, to: String): Double {
        return api.getCurrency(amount, from, to).await().toAmount
    }

    suspend fun connect() {
        val countryCode = locationHelper.getCountryCode()
        prefs.putLastLocale(countryCode)
    }
}