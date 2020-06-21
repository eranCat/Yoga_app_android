package com.erank.yogappl.ui.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.ArrayRes
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.withStyledAttributes
import androidx.core.view.setPadding
import com.erank.yogappl.R


abstract class EnumSpinner<E : Enum<*>> : AppCompatSpinner,
    AdapterView.OnItemSelectedListener {

    protected open var enumValue: E? = null
        set(value) {
            field = value
            value?.let { setSelectedItem(it) }
        }
    protected abstract val values: Array<E>

    @get:ArrayRes
    protected abstract val valuesArrayRes:Int

    private var listener: ((position: Int) -> Unit)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    init {
        setBackgroundResource(R.drawable.rounded_edittext)
        val scale = resources.displayMetrics.density
        val dpAsPixels = (4 * scale + 0.5f).toInt()
        setPadding(dpAsPixels)
        val stringArray = context.resources.getStringArray(valuesArrayRes)
        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, stringArray)
        layoutMode = MODE_DIALOG
    }

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
