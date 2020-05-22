package com.erank.yogappl.utils.custom_views

import android.R.layout.simple_spinner_dropdown_item
import android.content.Context
import android.util.AttributeSet
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.erank.yogappl.models.BaseData
import com.erank.yogappl.utils.extensions.cName


class DataLevelSpinner : EnumSpinner<BaseData.Level>,
    AdapterView.OnItemSelectedListener {

    public override var enumValue: BaseData.Level? = null
    override val values = BaseData.Level.values()

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    init {
        val levelNames = values.map { it.cName }
        adapter = ArrayAdapter(context, simple_spinner_dropdown_item, levelNames)
        onItemSelectedListener = this
        layoutMode = MODE_DIALOG
    }
}

