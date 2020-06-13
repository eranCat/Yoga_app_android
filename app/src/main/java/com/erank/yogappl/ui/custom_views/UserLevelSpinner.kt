package com.erank.yogappl.ui.custom_views

import android.content.Context
import android.util.AttributeSet
import android.widget.AdapterView
import com.erank.yogappl.R
import com.erank.yogappl.data.models.User


class UserLevelSpinner : EnumSpinner<User.Level> {

    public override var enumValue: User.Level? = null
    override val values = User.Level.values()
    override val valuesArrayRes = User.Level.resValues

    init {
        onItemSelectedListener = this
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

}

