package com.erank.yogappl.viewmodels

import androidx.lifecycle.ViewModel
import com.erank.yogappl.models.BaseData
import com.erank.yogappl.models.DataInfo
import com.erank.yogappl.models.LocationResult
import com.erank.yogappl.utils.helpers.MyImagePicker
import java.util.*

class NewEditDataActivityVM : ViewModel() {
    lateinit var dataInfo: DataInfo
    var data: BaseData? = null
    var selectedLocation: LocationResult? = null

    var result: MyImagePicker.Result? = null
    var selectedStartDate: Date? = null
    var selectedEndDate: Date? = null

    val canRemoveImage: Boolean
        get() = result?.hasImage ?: false

//TODO move db related stuff here

}
