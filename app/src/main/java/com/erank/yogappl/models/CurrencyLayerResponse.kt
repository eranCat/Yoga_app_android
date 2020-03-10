package com.erank.yogappl.models

import com.erank.yogappl.utils.SMap

data class CurrencyLayerResponse(
    val success: Boolean,
    val quotes: SMap<Double>,
    val error: Error
) {
    data class Error(
        val code: Int,
        val info: String
    )
}
