package com.erank.yogappl.utils.coroutines

import android.util.Log
import com.erank.yogappl.data.models.LocationResult
import com.erank.yogappl.data.models.TomtomLocationsResponse
import com.erank.yogappl.utils.OnLocationsFetchedCallback
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

class LocationsTask(private val url: String) {
    companion object {
        const val TAG = "LocationsTask"
    }

    fun start(callback: OnLocationsFetchedCallback) {
        CoroutineScope(IO).launch {
            val data = getData()
            withContext(Main) { callback(data) }
        }
    }


    private fun getData(): List<LocationResult> {
        try {
            val json = URL(url).readText()
            try {
                val response = Gson().fromJson(json, TomtomLocationsResponse::class.java)
                return response.results.sortedBy { it.distance }

            } catch (e: JsonSyntaxException) {
                Log.d(TAG, "JsonSyntaxException TomTom response")
                print(json)
            }

        } catch (e: IOException) {
            Log.d(TAG, "Url invalid", e)
        }

        return emptyList()
    }

}
