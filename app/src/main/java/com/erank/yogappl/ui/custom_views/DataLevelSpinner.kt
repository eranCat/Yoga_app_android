package com.erank.yogappl.ui.custom_views

import android.content.Context
import android.util.AttributeSet
import android.widget.AdapterView
import com.erank.yogappl.data.models.BaseData

class DataLevelSpinner : EnumSpinner<BaseData.Level>,
    AdapterView.OnItemSelectedListener {

    public override var enumValue: BaseData.Level? = null
    override val values = BaseData.Level.values()
    override val valuesArrayRes get() = BaseData.Level.resArray

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    init {
        onItemSelectedListener = this
    }
}

