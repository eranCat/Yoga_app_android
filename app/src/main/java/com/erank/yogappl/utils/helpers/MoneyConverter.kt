package com.erank.yogappl.utils.helpers

import android.net.Uri
import com.erank.yogappl.utils.coroutines.CurrencyTask
import com.erank.yogappl.utils.extensions.add
import com.erank.yogappl.utils.interfaces.MoneyConnectionCallback
import java.util.*
import java.util.Calendar.WEEK_OF_MONTH

class MoneyConverter(val sharedPrefs:SharedPrefsHelper) {

    companion object {
        private const val ApiKey = "ceb2a9d4119b6738d3fa4b8340d94adb"
        private const val BaseApi = "apilayer.net"
    }

    private var localeCurrencyMultiplier = 1f//1 dollar * x

    fun connect(callback: MoneyConnectionCallback) {

        val lastLocale = sharedPrefs.getLastLocale()
        if (lastLocale == Locale.getDefault().country) {

            val updateDate = sharedPrefs.getUpdatedDate()
            if (updateDate != null) {

                val weekAfter = Date(updateDate).add(WEEK_OF_MONTH, 1)

                if (weekAfter <= Date()) {
                    sharedPrefs.getMoney()?.let {
                        localeCurrencyMultiplier = it
                        callback.onSuccessConnectingMoney()
                        return
                    }
                }
            }
        }

        //            get current currency code from location
        val code = Currency.getInstance(Locale.getDefault()).currencyCode

        CurrencyTask(converterUrl(code)) {

            if (!it.success) {
                callback.onFailedConnectingMoney(it.error)
                return@CurrencyTask
            }

            localeCurrencyMultiplier = it.getUSD(code)!!
            saveMoneyOnSharedPrefs()
            callback.onSuccessConnectingMoney()

        }.start()
    }

    private fun saveMoneyOnSharedPrefs() {
        sharedPrefs.putLastLocale().putUpdatedDate()
            .putMoney(localeCurrencyMultiplier)
    }

    private fun converterUrl(code: String): String {
        return Uri.Builder()
            .scheme("http")
            .authority(BaseApi)
            .appendPath("api")
            .appendPath("live")
            .appendQueryParameter("access_key", ApiKey)
            .appendQueryParameter("currencies", code)
            .build()
            .toString()
    }

    fun convertFromLocaleToDefault(amount: Double) = amount / localeCurrencyMultiplier

    fun convertFromDefaultToLocale(amount: Double) = amount * localeCurrencyMultiplier
}