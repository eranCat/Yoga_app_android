package com.erank.yogappl.utils.helpers

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Resources
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.erank.yogappl.utils.OnLocationsFetchedCallback
import com.erank.yogappl.utils.coroutines.LocationsTask
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.*

class LocationHelper(val context: Context) {

    companion object {
        private const val VERSION = 2

        private const val RESULT_LIMIT = 50
        private const val SEARCH_RADIUS = 500 * 1_000//in meters : 500 KM
        private const val BASE_API = "api.tomtom.com"

        private const val KEY = "hEhWkGvw4i8xlpLfIfY6P3AA1cOBGutJ"
        private const val RPC_COARSE_LOCATION = 3
    }


    var lastKnownLocation: Location? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val currentLocale: Locale
        get() = Resources.getSystem().configuration.locales[0]


    fun getLocationIntent(latLng: LatLng): Intent? {
        // Create a Uri from an intent string. Use the result to create an Intent.
        val lat = latLng.latitude
        val lng = latLng.longitude

        val uri = Uri.parse("geo:$lat,${lng}?q=$lat,$lng")

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        return mapIntent.resolveActivity(context.packageManager)?.let { mapIntent }
    }

    fun getCountryCode(callback: (String, LatLng?) -> Unit) {
        getLastKnownLocation()
            .addOnFailureListener { callback(currentLocale.country, null) }
            .addOnSuccessListener {
                if (it == null) {
                    callback(currentLocale.country, null)
                    return@addOnSuccessListener
                }

                val geocoder = Geocoder(context, currentLocale)
                val locations = geocoder.getFromLocation(it.latitude, it.longitude, 1)

                val latLng = LatLng(it.latitude, it.longitude)

                if (locations.isNotEmpty()) {
                    callback(locations[0].countryCode, latLng)
                } else {
                    callback(currentLocale.country, latLng)
                }

            }
    }

    fun initLocationService() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    //https://api.tomtom.com/search/2/search/tel.json?typeahead=true&countrySet=IL&idxSet=POI&key=*****
    private fun buildUrl(
        query: String,
        countryCode: String,
        latLon: LatLng?
    ) = Uri.Builder()
        .scheme("https")
        .authority(BASE_API)
        .appendPath("search")
        .appendPath("$VERSION")
        .appendPath("search")
        .appendEncodedPath("$query.json")
        .appendQueryParameter("typeahead", "true")
        .appendQueryParameter("language", currentLocale.toLanguageTag())
        .appendQueryParameter("limit", "$RESULT_LIMIT")
        .appendQueryParameter("countrySet", countryCode)
        .apply {
            latLon?.let {
                appendQueryParameter("lat", "${it.latitude}")
                appendQueryParameter("lon", "${it.longitude}")
                appendQueryParameter("radius", "$SEARCH_RADIUS")
            }
        }
        .appendQueryParameter("key", KEY)
        .toString()


    private fun getLastKnownLocation() =
        fusedLocationClient!!.lastLocation
            .addOnSuccessListener { lastKnownLocation = it }

    fun getLocationResults(
        query: String,
        callback: OnLocationsFetchedCallback
    ) = getCountryCode { code, latLon ->
        LocationsTask(buildUrl(query, code, latLon)).start(callback)
    }

    fun getLocationPermissionIfNeeded(activity: Activity): Boolean {
        val per = ACCESS_COARSE_LOCATION

        if (ContextCompat.checkSelfPermission(activity, per) == PERMISSION_GRANTED)
            return true

        ActivityCompat.requestPermissions(
            activity,
            arrayOf(per),
            RPC_COARSE_LOCATION
        )
        return false
    }


    private fun checkPermissionResults(
        permissions: Array<String>, results: IntArray
    ): Boolean {

        val index = permissions.indexOf(ACCESS_COARSE_LOCATION)

        return when {
            index == -1 -> false
            results[index] != PERMISSION_GRANTED -> false
            ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION)
                    != PERMISSION_GRANTED ->
                false
            else -> true
        }

    }

    fun checkAllPermissionResults(
        requestCode: Int, permissions: Array<String>, results: IntArray
    ) = when (requestCode) {
        RPC_COARSE_LOCATION -> checkPermissionResults(permissions, results)
        else -> false
    }

}