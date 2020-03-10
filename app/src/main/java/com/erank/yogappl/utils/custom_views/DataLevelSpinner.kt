package com.erank.yogappl.utils.custom_views

import android.R.layout.simple_spinner_dropdown_item
import android.content.Context
import android.content.res.Resources
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

    constructor(context: Context, mode: Int) : super(context, mode)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    constructor(
        context: Context,
        attributeSet: AttributeSet?,
        defStyle: Int,
        mode: Int
    ) : super(context, attributeSet, defStyle, mode)

    constructor(
        context: Context,
        attributeSet: AttributeSet?,
        defStyle: Int,
        mode: Int,
        theme: Resources.Theme
    ) : super(context, attributeSet, defStyle, mode, theme)

    init {
        val levelNames = values.map { it.cName }
        adapter = ArrayAdapter(context, simple_spinner_dropdown_item, levelNames)
        onItemSelectedListener = this
        layoutMode = MODE_DIALOG
    }
}

