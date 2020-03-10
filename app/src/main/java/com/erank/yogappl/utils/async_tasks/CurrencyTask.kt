package com.erank.yogappl.utils.async_tasks

import android.os.AsyncTask
import com.erank.yogappl.models.CurrencyLayerResponse
import com.erank.yogappl.utils.OnResponseRetrievedCallback
import com.google.gson.Gson
import java.net.URL

class CurrencyTask(
    private val callback: OnResponseRetrievedCallback<CurrencyLayerResponse>
) : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg urls: String?): String {
        return URL(urls.first()).readText()
    }

    override fun onPostExecute(result: String) {
        val response = Gson().fromJson(result, CurrencyLayerResponse::class.java)
        callback.invoke(response)
    }
}
