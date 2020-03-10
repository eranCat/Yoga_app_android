package com.erank.yogappl.utils.interfaces

import android.text.Editable
import android.text.TextWatcher

interface EditTextOnChangedAdapter : TextWatcher {

    fun afterTextChanged(text: String)

    override fun afterTextChanged(s: Editable) = afterTextChanged(s.toString())

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
}