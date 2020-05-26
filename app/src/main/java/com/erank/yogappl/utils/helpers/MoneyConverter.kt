package com.erank.yogappl.utils.helpers

import com.erank.yogappl.data.network.CurrencyLayerApi
import com.erank.yogappl.data.repository.SharedPrefsHelper
import com.erank.yogappl.utils.extensions.add
import com.erank.yogappl.utils.interfaces.MoneyConnectionCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        val lastLocale = sharedPrefs.getLastLocale()
        if (lastLocale == Locale.getDefault().country) {

            val updateDate = sharedPrefs.getUpdatedDate()
            if (updateDate != null) {

                val weekAfter = Date(updateDate).add(WEEK_OF_MONTH, 1)

                if (weekAfter <= Date()) {
                    sharedPrefs.getMoney()?.let {
                        localeCurrencyMultiplier = it
                        return
                    }
                }
            }
        }

        //            get current currency code from location
        val code = Currency.getInstance(Locale.getDefault()).currencyCode

        val response = api.getCurrencyCodes(code).await()
        if (!response.success) {
            throw Exception(response.error.toString())
        }

        localeCurrencyMultiplier = response.getUSD(code)!!
        saveMoneyOnSharedPrefs()
    }

    private fun saveMoneyOnSharedPrefs() = sharedPrefs
        .putLastLocale().putUpdatedDate().putMoney(localeCurrencyMultiplier)

}