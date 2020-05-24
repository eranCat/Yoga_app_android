package com.erank.yogappl.utils.interfaces

import com.erank.yogappl.data.models.CurrencyLayerResponse

interface MoneyConnectionCallback {
    fun onSuccessConnectingMoney()
    fun onFailedConnectingMoney(error: CurrencyLayerResponse.Error)
}