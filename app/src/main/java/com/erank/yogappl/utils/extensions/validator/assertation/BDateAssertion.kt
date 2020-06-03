package com.erank.yogappl.utils.extensions.validator.assertation

import com.afollestad.vvalidator.assertion.Assertion
import com.erank.yogappl.ui.custom_views.BirthDateTextView

class BDateAssertion : Assertion<BirthDateTextView, BDateAssertion>() {
    override fun defaultDescription(): String {
        return "Needs to be between 16-120 years old"
    }

    override fun isValid(view: BirthDateTextView): Boolean {
        return view.date != null
    }

}