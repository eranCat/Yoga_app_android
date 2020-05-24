package com.erank.yogappl.ui.custom_views

import android.R.layout.simple_spinner_item
import android.content.Context
import android.util.AttributeSet
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.erank.yogappl.data.models.User
import com.erank.yogappl.utils.extensions.cName

class UserTypeSpinner @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : EnumSpinner<User.Type>(context, attrs, defStyleAttr),
    AdapterView.OnItemSelectedListener {

    public override var enumValue: User.Type? = null
    override val values = User.Type.values()

    init {
        val typeNames = values.map { it.cName }
        adapter = ArrayAdapter(context, simple_spinner_item, typeNames)
        onItemSelectedListener = this
    }
}