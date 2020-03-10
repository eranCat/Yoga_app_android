package com.erank.yogappl.utils.interfaces

import com.erank.yogappl.models.LocationResult

interface OnLocationSelectedCallback {
    fun onSelected(location: LocationResult)
}