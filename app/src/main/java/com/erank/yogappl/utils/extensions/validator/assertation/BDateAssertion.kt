package com.erank.yogappl.utils.extensions.validator.assertation

import com.afollestad.vvalidator.assertion.Assertion
import com.erank.yogappl.ui.custom_views.BirthDateTextView
import com.erank.yogappl.utils.extensions.minusYears
import java.util.*

class BDateAssertion : Assertion<BirthDateTextView, BDateAssertion>() {
    override fun defaultDescription(): String {
        return "Needs to be between 16-120 years old"
    }

    override fun isValid(view: BirthDateTextView): Boolean {
        val date = view.date ?: return false
        val today = Date()
        return date >= today.minusYears(120) && date <= today.minusYears(16)
    }

}