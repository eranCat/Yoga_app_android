package com.erank.yogappl.utils.extensions.validator.assertation

import android.widget.NumberPicker
import com.afollestad.vvalidator.assertion.Assertion
import com.erank.yogappl.R

class MaxNumberPickerAssertion : Assertion<NumberPicker, MaxNumberPickerAssertion>() {
    override fun isValid(view: NumberPicker): Boolean {
        return view.value > 0
    }

    override fun defaultDescription() = "Should be positive"
}