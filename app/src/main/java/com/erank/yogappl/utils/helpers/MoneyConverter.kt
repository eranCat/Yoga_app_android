package com.erank.yogappl.utils.helpers

import com.erank.yogappl.data.network.CurrencyLayerApi
import com.erank.yogappl.data.repository.SharedPrefsHelper
import com.erank.yogappl.utils.extensions.add
import java.util.*
import java.util.Calendar.WEEK_OF_MONTH

class MoneyConverter(
    val api: CurrencyLayerApi,
    val sharedPrefs: SharedPrefsHelper
) {

    companion object {
        private var localeCurrencyMultiplier = 1f//1 dollar * x

        fun convertFromLocaleToDefault(amount: Double) = amount / localeCurrencyMultiplier

        fun convertFromDefaultToLocale(amount: Double) = amount * localeCurrencyMultiplier
    }


    suspend fun connect() {
//        if locale hasn't changed since last time
        sharedPrefs.getUpdatedDate()?.let { timestamp ->
            val weekAfter = Date(timestamp).add(WEEK_OF_MONTH, 1)
            val today = Date()
            if (today.before(weekAfter)) {
                sharedPrefs.getMoney()?.let {
                    localeCurrencyMultiplier = it
                    return
                }
            }
        }
//            get current currency code from location
        val code = Currency.getInstance(Locale.getDefault()).currencyCode
        val response = api.getCurrencyCodes(code).await()
        response.error?.let {
            throw it.toException()
        }

        localeCurrencyMultiplier = response.getUSD(code)!!
        saveMoneyOnSharedPrefs()
    }

    private fun saveMoneyOnSharedPrefs() = sharedPrefs.putMoney(localeCurrencyMultiplier)

}