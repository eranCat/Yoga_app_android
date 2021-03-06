package com.erank.yogappl.data.models

import com.erank.yogappl.utils.SMap

data class CurrencyLayerResponse(
    val success: Boolean,
    val quotes: SMap<Float>,
    val error: Error?
) {
    data class Error(
        val code: Int,
        val info: String
    ) {
        fun toException() = Exception("$info , code: $code")
    }

    fun getUSD(code: String) = quotes["USD$code"]
}
