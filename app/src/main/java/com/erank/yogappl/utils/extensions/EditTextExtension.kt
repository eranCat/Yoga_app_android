package com.erank.yogappl.utils.extensions

import android.widget.EditText
import com.erank.yogappl.utils.interfaces.EditTextOnChangedAdapter

/**
 * Extension function to simplify setting an setTextChangedListener action to EditText components.
 */
fun EditText.setTextChangedListener(afterTextChanged: (text: String) -> Unit) {
    addTextChangedListener(object : EditTextOnChangedAdapter {
        override fun afterTextChanged(text: String) {
            afterTextChanged.invoke(text)
        }
    })
}

val EditText.txt get() = text.toString()