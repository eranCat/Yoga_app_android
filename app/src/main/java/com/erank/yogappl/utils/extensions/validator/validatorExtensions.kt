package com.erank.yogappl.utils.extensions.validator

import android.widget.NumberPicker
import androidx.annotation.IdRes
import com.afollestad.vvalidator.checkAttached
import com.afollestad.vvalidator.field.FieldBuilder
import com.afollestad.vvalidator.form.Form
import com.afollestad.vvalidator.form.GenericFormField
import com.afollestad.vvalidator.getViewOrThrow
import com.erank.yogappl.ui.custom_views.BirthDateTextView
import com.erank.yogappl.utils.extensions.validator.field.BirthDateField
import com.erank.yogappl.utils.extensions.validator.field.PickerField


fun Form.picker(
    view: NumberPicker,
    name: String? = null,
    builder: FieldBuilder<PickerField>
): GenericFormField {
    val newField = PickerField(container.checkAttached(), view, name)
    builder(newField)
    return appendField(newField)
}

fun Form.picker(
    @IdRes id: Int,
    name: String? = null,
    builder: FieldBuilder<PickerField>
) = picker(
    view = container.getViewOrThrow(id),
    name = name,
    builder = builder
)


fun Form.birthDatePicker(
    view: BirthDateTextView,
    name: String? = null,
    builder: FieldBuilder<BirthDateField>
): GenericFormField {
    val newField = BirthDateField(container.checkAttached(), view, name)
    builder(newField)
    return appendField(newField)
}


fun Form.birthDatePicker(
    @IdRes id: Int,
    name: String? = null,
    builder: FieldBuilder<BirthDateField>
) = birthDatePicker(
    view = container.getViewOrThrow(id),
    name = name,
    builder = builder
)
