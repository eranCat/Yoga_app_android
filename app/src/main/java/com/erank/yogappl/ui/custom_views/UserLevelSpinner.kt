package com.erank.yogappl.ui.custom_views

import android.R.layout.simple_spinner_item
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.erank.yogappl.data.models.User
import com.erank.yogappl.utils.extensions.cName


class UserLevelSpinner : EnumSpinner<User.Level>, AdapterView.OnItemSelectedListener {

    public override var enumValue: User.Level? = null
    override val values = User.Level.values()

    init {
        val levelNames = values.map { it.cName }
        adapter = ArrayAdapter(context, simple_spinner_item, levelNames)
        onItemSelectedListener = this
    }

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
}

