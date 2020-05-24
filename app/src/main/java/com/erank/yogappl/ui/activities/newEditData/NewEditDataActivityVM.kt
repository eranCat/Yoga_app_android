package com.erank.yogappl.ui.activities.newEditData

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.data.models.DataInfo
import com.erank.yogappl.data.models.LocationResult
import java.util.*

class NewEditDataActivityVM : ViewModel() {
    lateinit var dataInfo: DataInfo
    var data: BaseData? = null
    var selectedLocation: LocationResult? = null

    var selectedEventImgBitmap: Bitmap? = null
    var selectedEventImgUrl: String? = null
    var selectedLocalEventImg: Uri? = null
    var selectedStartDate: Date? = null
    var selectedEndDate: Date? = null

    val canRemoveImage: Boolean
        get() = (selectedEventImgUrl != null
                || selectedLocalEventImg != null
                || selectedEventImgBitmap != null)

//TODO move db related stuff here

}
