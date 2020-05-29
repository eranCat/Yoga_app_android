package com.erank.yogappl.utils.extensions

import com.google.android.gms.maps.model.LatLng

val LatLng.mapped: Map<String, Any>
    get() = mapOf(
        "lat" to latitude,
        "lon" to longitude
    )

fun LatLng(json: Map<String, Any>): LatLng {
    val lat = json["lat"] as Double
    val lon = json["lon"] as Double
    return LatLng(lat, lon)
}