package com.erank.yogappl.utils.interfaces

import com.erank.yogappl.data.models.LocationResult

interface OnLocationSelectedCallback {
    fun onSelected(location: LocationResult)
}