package com.erank.yogappl.utils.extensions

import com.google.android.gms.maps.model.LatLng

val LatLng.mapped
    get() = mutableMapOf(
        "lat" to latitude,
        "lon" to longitude
    )

fun newLatLng(json: MutableMap<String, Double>) =
    LatLng(json["lat"]!!, json["lon"]!!)