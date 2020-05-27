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
import com.erank.yogappl.R
import com.erank.yogappl.data.models.LocationResult
import com.erank.yogappl.data.network.TomTomApi
import com.erank.yogappl.utils.extensions.await
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.*

class LocationHelper(val context: Context, val api: TomTomApi) {

    companion object {
        private const val RPC_COARSE_LOCATION = 3
    }

    private var lastKnownLocation: Location? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val currentLocale: Locale
        get() = Resources.getSystem().configuration.locales[0]

    private val supportedCodes = context.resources
        .getStringArray(R.array.supportedTomTomCodes)

    fun getLocationIntent(latLng: LatLng): Intent? {
        // Create a Uri from an intent string. Use the result to create an Intent.
        val lat = latLng.latitude
        val lng = latLng.longitude

        val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        return mapIntent.resolveActivity(context.packageManager)?.let { mapIntent }
    }

    suspend fun getCountryCode(): Pair<String, LatLng?> {
        val location = getLastKnownLocation().await()
            ?: return Pair(currentLocale.country, null)

        val latLng = LatLng(location.latitude, location.longitude)

        val locations = Geocoder(context, currentLocale)
            .getFromLocation(latLng.latitude, latLng.longitude, 1)

        val country = if (locations.isNotEmpty())
            locations[0].countryCode
        else
            currentLocale.country

        return Pair(country, latLng)
    }

    fun initLocationService() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    private fun getLastKnownLocation() =
        fusedLocationClient!!.lastLocation
            .addOnSuccessListener { lastKnownLocation = it }

    suspend fun getLocationResults(query: String): List<LocationResult> {
        val (countryCode, latLng) = getCountryCode()

        val languageTag = currentLocale.toLanguageTag()

        val language =
            if (supportedCodes.contains(languageTag))
                languageTag
            else null

        val lat = latLng?.latitude
        val lon = latLng?.longitude
        return api
            .searchAsync(query, countryCode, language, lat, lon)
            .await()
            .results
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