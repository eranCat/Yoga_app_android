package com.erank.yogappl.utils.helpers

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.erank.yogappl.utils.OnLocationsFetchedCallback
import com.erank.yogappl.utils.async_tasks.LocationsTask
import com.erank.yogappl.utils.extensions.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.*

object LocationHelper {

    var lastKnownLocation: Location? = null
    const val RPC_FINE_LOCATION = 2
    const val RPC_COARSE_LOCATION = 3

    fun getLocationIntent(
        packageManager: PackageManager,
        latLng: LatLng
    ): Intent? {
        // Create a Uri from an intent string. Use the result to create an Intent.
        val lat = latLng.latitude
        val lng = latLng.longitude

        val uri = Uri.parse("geo:$lat,${lng}?q=$lat,$lng")

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        return mapIntent.resolveActivity(packageManager)?.let { mapIntent }
    }

    private const val version = 2
    private const val resLimit = 5
    private const val searchRadius = 50_000//in meters : 50 KM

    private const val BASE_API = "api.tomtom.com"
    private const val KEY = "hEhWkGvw4i8xlpLfIfY6P3AA1cOBGutJ"

    private var fusedLocationClient: FusedLocationProviderClient? = null


    val currentLocale: Locale
        get() = Resources.getSystem().configuration.locales[0]


    fun initLocationService(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    private fun locationsUrl(query: String, callback: (Uri?) -> Unit) {
        getLastKnownLocation()
            .addOnSuccessListener {
                it ?: run {
                    callback.invoke(null)
                    return@addOnSuccessListener
                }

                val lat = it.latitude.toString()
                val lon = it.longitude.toString()


                val uri = Uri.Builder()
                    .scheme("https")
                    .authority(BASE_API)
                    .appendPath("search")
                    .appendPath("$version")
                    .appendPath("search")
                    .appendEncodedPath("$query.json")
                    .appendQueryParameter("key", KEY)
                    .appendQueryParameter("limit", "$resLimit")
                    .appendQueryParameter("countrySet", currentLocale.country)
                    .appendQueryParameter("lat", lat)
                    .appendQueryParameter("lon", lon)
                    .appendQueryParameter("radius", "$searchRadius")
                    .build()

                callback.invoke(uri)
            }.addOnFailureListener { callback.invoke(null) }
    }

    private fun getLastKnownLocation() =
        fusedLocationClient!!.lastLocation
            .addOnSuccessListener {

                lastKnownLocation = it
            }

    fun getLocationResults(query: String, callback: OnLocationsFetchedCallback) {
        locationsUrl(query) {

            it?.let {
                LocationsTask(callback).execute(it.toString())
            } ?: callback.invoke(mutableListOf())

        }
    }

    fun getFineLocationPermissionIfNeeded(activity: Activity) =
        askPermissionIfNeeded(activity, ACCESS_FINE_LOCATION)

    fun getCoarseLocationPermissionIfNeeded(activity: Activity) =
        askPermissionIfNeeded(activity, ACCESS_COARSE_LOCATION)


    private fun askPermissionIfNeeded(activity: Activity, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PERMISSION_GRANTED
        )
            return true


        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {

            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                RPC_FINE_LOCATION
            )

        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                RPC_FINE_LOCATION
            )
        }

        return false
    }

    fun checkPermissionResultsCoarseLocation(
        context: Context,
        permissions: Array<out String>,
        results: IntArray
    ) = checkPermissionResults(context, permissions,
            results, ACCESS_COARSE_LOCATION)

    fun checkPermissionResultsFineLocation(
        context: Context,
        permissions: Array<out String>,
        results: IntArray
    ) = checkPermissionResults(context, permissions,
            results, ACCESS_FINE_LOCATION)


    private fun checkPermissionResults(
        context: Context, permissions: Array<out String>,
        results: IntArray, permission: String
    ): Boolean {

        val indexOf = permissions.indexOf(permission)
        if (indexOf == -1) return false

        if (results.getOrNull(indexOf) != PERMISSION_GRANTED) {
            context.toast("Permission Denied")
            return false
        }

        if (ContextCompat.checkSelfPermission(context, permission) != PERMISSION_GRANTED)
            return false

        context.toast("Permission Granted")
        return true
    }
}