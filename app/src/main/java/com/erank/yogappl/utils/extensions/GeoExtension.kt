package com.erank.yogappl.utils.extensions

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import org.imperiumlabs.geofirestore.GeoFirestore

fun GeoFirestore.setLocation(docID: String?, location: LatLng) {
    val point = GeoPoint(location.latitude, location.longitude)
    setLocation(docID, point, object : GeoFirestore.CompletionCallback {
        override fun onComplete(exception: Exception?) {
            exception?.let { throw it }
        }
    })
}