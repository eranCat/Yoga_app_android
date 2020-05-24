package com.erank.yogappl.utils.helpers

import android.content.Context
import android.net.Uri
import com.erank.yogappl.utils.coroutines.CurrencyTask
import com.erank.yogappl.utils.extensions.add
import com.erank.yogappl.utils.interfaces.MoneyConnectionCallback
import java.util.*
import java.util.Calendar.WEEK_OF_MONTH

object MoneyConverter {

    private val LAST_LOCALE = "last_locale"
    private const val ApiKey = "ceb2a9d4119b6738d3fa4b8340d94adb"
    private const val BaseApi = "apilayer.net"

    private const val UPDATED_DATE = "moneyLastUpdatedDate"
    private const val MONEY = "money"

    private var localeCurrencyMultiplier = 1f//1 dollar * x

    fun connect(
        context: Context,
        callback: MoneyConnectionCallback
    ) {
        val prefs = SharedPrefsHelper.Builder(context)

        val lastLocale = prefs.getString(LAST_LOCALE, null)
        if (lastLocale == Locale.getDefault().country) {

            val updateDate = prefs.getLong(UPDATED_DATE)
            if (updateDate != null) {

                val weekAfter = Date(updateDate).add(WEEK_OF_MONTH, 1)

                if (weekAfter <= Date()) {
                    val money = prefs.getFloat(MONEY)
                    if (money != null) {
                        localeCurrencyMultiplier = money
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
            saveMoneyOnSharedPrefs(context)
            callback.onSuccessConnectingMoney()

        }.start()
    }

    private fun saveMoneyOnSharedPrefs(context: Context) {
        SharedPrefsHelper.Builder(context)
            .put(MONEY, localeCurrencyMultiplier)
            .put(UPDATED_DATE, Date().time)
            .put(LAST_LOCALE, Locale.getDefault().country)
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