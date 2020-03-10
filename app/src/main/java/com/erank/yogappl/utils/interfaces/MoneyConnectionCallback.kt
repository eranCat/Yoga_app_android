package com.erank.yogappl.utils.interfaces

import com.erank.yogappl.models.CurrencyLayerResponse

interface MoneyConnectionCallback {
    fun onSuccessConnectingMoney()
    fun onFailedConnectingMoney(error: CurrencyLayerResponse.Error)
}