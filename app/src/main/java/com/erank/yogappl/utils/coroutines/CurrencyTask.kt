package com.erank.yogappl.utils.coroutines

import com.erank.yogappl.models.CurrencyLayerResponse
import com.erank.yogappl.utils.OnResponseRetrievedCallback
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class CurrencyTask(
    private val url: String,
    private val callback: OnResponseRetrievedCallback<CurrencyLayerResponse>
) {
    fun start() = CoroutineScope(IO).launch {
        val txtFromUrl = URL(url).readText()
        val response = Gson().fromJson(txtFromUrl, CurrencyLayerResponse::class.java)
        withContext(Main) { callback(response) }
    }

}