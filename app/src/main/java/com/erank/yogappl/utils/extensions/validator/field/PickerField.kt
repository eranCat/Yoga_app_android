package com.erank.yogappl.utils.extensions.validator.field

import android.widget.NumberPicker
import com.afollestad.vvalidator.ValidationContainer
import com.afollestad.vvalidator.field.FieldValue
import com.afollestad.vvalidator.field.FormField
import com.afollestad.vvalidator.field.IntFieldValue
import com.erank.yogappl.utils.extensions.validator.assertation.MaxNumberPickerAssertion

class PickerField(
    container: ValidationContainer,
    view: NumberPicker,
    name: String?
) : FormField<PickerField, NumberPicker, Int>(container, view, name) {
    init {
        onErrors { myView, errors ->
            // Do some sort of default error handling with views
        }
    }

    // Your first custom assertion
    fun myAssertion() = assert(MaxNumberPickerAssertion())

    override fun obtainValue(id: Int, name: String): FieldValue<Int> {
        return IntFieldValue(
            id = id,
            name = name,
            value = view.value
        )
    }

    override fun startRealTimeValidation(debounce: Int) {
        // See the "Real Time Validation" section below.
        // You'd want to begin observing input to the view this field attaches to,
        // and call `validate()` on this field when it changes. You should respect the
        // `debounce` parameter as well.
    }
}