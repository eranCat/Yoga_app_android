package com.erank.yogappl.utils.helpers

import android.content.Context
import android.net.Uri
import com.erank.yogappl.utils.async_tasks.CurrencyTask
import com.erank.yogappl.utils.extensions.add
import com.erank.yogappl.utils.interfaces.MoneyConnectionCallback
import java.util.*

object MoneyConverter {

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

        prefs.getLong(UPDATED_DATE)?.let { timestamp ->

            val weekAfter = Date(timestamp).add(Calendar.WEEK_OF_MONTH, 1)
            val aWeekHasNotPassed = weekAfter <= Date()

            if (aWeekHasNotPassed) {
                prefs.getFloat(MONEY)?.let {
                    localeCurrencyMultiplier = it
                    callback.onSuccessConnectingMoney()
                    return
                }
            }
        }

        //            get current currency code from location
        val code = Currency.getInstance(Locale.getDefault()).currencyCode

        CurrencyTask {

            if (!it.success) {
                callback.onFailedConnectingMoney(it.error)
                return@CurrencyTask
            }

            localeCurrencyMultiplier = it.quotes["USD$code"]?.toFloat()!!
            saveMoneyOnSharedPrefs(context)
            callback.onSuccessConnectingMoney()

        }.execute(converterUrl(code))
    }

    private fun saveMoneyOnSharedPrefs(context: Context) {
        SharedPrefsHelper.Builder(context)
            .put(MONEY, localeCurrencyMultiplier)
            .put(UPDATED_DATE, Date().time)
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