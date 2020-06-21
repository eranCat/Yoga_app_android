package com.erank.yogappl.utils.extensions.validator.field

import com.afollestad.vvalidator.ValidationContainer
import com.afollestad.vvalidator.field.FieldValue
import com.afollestad.vvalidator.field.FormField
import com.erank.yogappl.ui.custom_views.BirthDateTextView
import java.util.*

class BirthDateField(
    container: ValidationContainer,
    view: BirthDateTextView,
    name: String?
) : FormField<BirthDateField, BirthDateTextView, Date>(container, view, name) {

    override fun obtainValue(id: Int, name: String): FieldValue<Date> {
        return FieldValue(
            id, name, view.date ?: Date(), Date::class
        )
    }

    override fun startRealTimeValidation(debounce: Int) {
        // See the "Real Time Validation" section below.
        // You'd want to begin observing input to the view this field attaches to,
        // and call `validate()` on this field when it changes. You should respect the
        // `debounce` parameter as well.
    }
}
