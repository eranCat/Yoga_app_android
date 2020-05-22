package com.erank.yogappl.utils.custom_views

import android.R.layout.simple_spinner_item
import android.content.Context
import android.util.AttributeSet
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.erank.yogappl.models.User
import com.erank.yogappl.utils.extensions.cName

class UserTypeSpinner : EnumSpinner<User.Type>, AdapterView.OnItemSelectedListener {

    public override var enumValue: User.Type? = null
    override val values: Array<User.Type> = User.Type.values()

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    init{
        val typeNames = values.map { it.cName }
        adapter = ArrayAdapter(context, simple_spinner_item, typeNames)
        onItemSelectedListener = this
    }

}