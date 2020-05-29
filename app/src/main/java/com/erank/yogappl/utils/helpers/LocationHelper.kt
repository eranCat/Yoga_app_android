package com.erank.yogappl.utils.helpers

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.erank.yogappl.R
import com.erank.yogappl.data.models.LocationResult
import com.erank.yogappl.data.network.TomTomApi
import com.erank.yogappl.utils.extensions.await
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.*

class LocationHelper(val context: Context, val api: TomTomApi) {

    companion object {
        private const val RPC_COARSE_LOCATION = 3
    }

    private var fusedLocationClient= LocationServices
        .getFusedLocationProviderClient(context)

    private val currentLocale: Locale
        get() = context.resources.configuration.locales[0]

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

    suspend fun getLastKnownLocation() = fusedLocationClient!!.lastLocation.await()

    suspend fun getCountryCode(): String{
        val location = getLastKnownLocation() ?: return "IL"
        val locations = getLocationAddress(location)
        return locations.getOrNull(0)?.countryCode ?: "IL"
    }

    private fun getLocationAddress(location: Location): List<Address> {
        val lat = location.latitude
        val lon = location.longitude
        return Geocoder(context).getFromLocation(lat, lon, 1)
    }

    suspend fun getLocationResults(query: String): List<LocationResult> {
        val languageTag = currentLocale.toLanguageTag()

        val language = languageTag.takeIf {
            supportedCodes.contains(languageTag)
        }

        val loc = getLastKnownLocation()
        val lat = loc?.latitude
        val lon = loc?.longitude
        val countryCode = getCountryCode()

        val response = api.searchAsync(query, countryCode, language, lat, lon).await()
        return response.results
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