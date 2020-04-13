package com.erank.yogappl.models

import android.os.Parcelable
import com.erank.yogappl.utils.enums.DataType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataInfo(
    val type: DataType,
    var id: String? = null
) : Parcelable