package com.erank.yogappl.ui.custom_views

import android.content.Context
import android.util.AttributeSet
import android.widget.AdapterView
import com.erank.yogappl.R
import com.erank.yogappl.data.models.User

class UserTypeSpinner : EnumSpinner<User.Type>, AdapterView.OnItemSelectedListener {

    public override var enumValue: User.Type? = null
    override val values: Array<User.Type> = User.Type.values()
    override val valuesArrayRes get() = User.Type.resValues

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    init {
        onItemSelectedListener = this
    }

}