package com.erank.yogappl.ui.activities.location

import androidx.lifecycle.ViewModel
import com.erank.yogappl.utils.OnLocationsFetchedCallback
import com.erank.yogappl.utils.helpers.LocationHelper
import javax.inject.Inject

class LocationPickerVM @Inject constructor(val locationHelper: LocationHelper):ViewModel(){
    fun getLocationResults(
        query: String, callback: OnLocationsFetchedCallback
    ) {
        locationHelper.getLocationResults(query,callback)
    }

}