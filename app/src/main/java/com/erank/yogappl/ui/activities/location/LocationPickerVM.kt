package com.erank.yogappl.ui.activities.location

import androidx.lifecycle.ViewModel
import com.erank.yogappl.utils.helpers.LocationHelper
import javax.inject.Inject

class LocationPickerVM @Inject constructor(val locationHelper: LocationHelper) : ViewModel() {
    suspend fun getLocationResults(query: String) =
        locationHelper.getLocationResults(query)
}