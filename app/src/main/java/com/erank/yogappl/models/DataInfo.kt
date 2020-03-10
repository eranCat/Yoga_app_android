package com.erank.yogappl.models

import android.os.Parcelable
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.enums.SourceType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataInfo(
    val dataType: DataType,
    val sourceType: SourceType,
    var position: Int?
) : Parcelable {
    constructor(dataType: DataType) : this(dataType,SourceType.ALL,null)//new
}