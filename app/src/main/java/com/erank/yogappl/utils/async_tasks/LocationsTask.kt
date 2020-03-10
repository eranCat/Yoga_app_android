package com.erank.yogappl.utils.async_tasks

import android.os.AsyncTask
import android.util.Log
import com.erank.yogappl.models.LocationError
import com.erank.yogappl.models.LocationResult
import com.erank.yogappl.models.TomtomLocationsResponse
import com.erank.yogappl.utils.OnLocationsFetchedCallback
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.IOException
import java.net.URL

class LocationsTask(
    private val callback: OnLocationsFetchedCallback
) : AsyncTask<String, Void, List<LocationResult>>() {
    companion object {
        const val TAG = "LocationsTask"
    }

    override fun doInBackground(vararg urls: String?): List<LocationResult> {
        try {
            val json = URL(urls.first()).readText()

            val gson = Gson()

            try {
                val response = gson.fromJson(json, TomtomLocationsResponse::class.java)
                return response.results.sortedBy { it.distance }
            } catch (e: JsonSyntaxException) {

                val errorResponse = gson.fromJson(json, LocationError::class.java)
                Log.d(TAG, errorResponse.toString())
            }

        } catch (e: IOException) {
            Log.d(TAG, "Url problem", e)
        }

        return emptyList()
    }

    override fun onPostExecute(result: List<LocationResult>) {
        callback.invoke(result)
    }

}
