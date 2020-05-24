package com.erank.yogappl.data.models

import android.os.Parcelable
import com.erank.yogappl.data.enums.DataType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataInfo(
    val type: DataType,
    var id: String? = null
) : Parcelable