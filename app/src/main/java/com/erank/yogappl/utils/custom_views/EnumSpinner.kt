package com.erank.yogappl.utils.custom_views

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.setPadding
import com.erank.yogappl.R


abstract class EnumSpinner<E : Enum<E>> : AppCompatSpinner,
    AdapterView.OnItemSelectedListener {

    protected open var enumValue: E? = null
        set(value) {
            field = value
            value?.let { setSelectedItem(it) }
        }
    protected abstract val values: Array<E>

    var listener: ((position: Int) -> Unit)? = null

    init {
        setBackgroundResource(R.drawable.rounded_edittext)
        val scale = resources.displayMetrics.density
        val dpAsPixels = (4 * scale + 0.5f).toInt()
        setPadding(dpAsPixels)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    fun setOnItemSelectedListener(listener: (position: Int) -> Unit) {
        super.setOnItemSelectedListener(this)
        this.listener = listener
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        enumValue = values[position]
        listener?.invoke(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        enumValue = null
        listener?.invoke(-1)
    }

    fun setSelectedItem(enumValue: E) {
        this.enumValue = enumValue
        setSelection(enumValue.ordinal, true)
    }
}
