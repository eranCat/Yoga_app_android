package com.erank.yogappl.data.models

import com.google.gson.annotations.SerializedName

class CurrencyModel(
    val from: String,
    val to: String,
    @SerializedName("from_amount")
    val fromAmount: Double,
    @SerializedName("to_amount")
    val toAmount: Double
)