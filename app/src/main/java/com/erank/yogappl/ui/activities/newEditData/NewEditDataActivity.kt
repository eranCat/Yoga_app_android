package com.erank.yogappl.ui.activities.newEditData

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.afollestad.vvalidator.form
import com.afollestad.vvalidator.form.FormResult
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.erank.yogappl.R
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.enums.TextFieldValidStates.*
import com.erank.yogappl.data.models.*
import com.erank.yogappl.ui.activities.location.LocationPickerActivity
import com.erank.yogappl.ui.custom_views.ProgressDialog
import com.erank.yogappl.utils.App
import com.erank.yogappl.utils.DateValidationPredicate
import com.erank.yogappl.utils.OnDateSet
import com.erank.yogappl.utils.extensions.*
import com.erank.yogappl.utils.extensions.validator.assertation.MaxNumberPickerAssertion
import com.erank.yogappl.utils.extensions.validator.picker
import com.erank.yogappl.utils.helpers.BaseDataValidator
import com.erank.yogappl.utils.helpers.MyImagePicker
import com.erank.yogappl.utils.interfaces.ImagePickerCallback
import com.erank.yogappl.utils.runOnBackground
import kotlinx.android.synthetic.main.activity_new_edit_data.*
import java.text.DateFormat.MEDIUM
import java.text.DateFormat.SHORT
import java.util.*
import javax.inject.Inject

class NewEditDataActivity : AppCompatActivity(), ImagePickerCallback {

    companion object {
        private val TAG = NewEditDataActivity::class.java.name
        const val RC_LOCATION = 11
        private const val TITLE_KEY = "title"
        private const val EQUIP_KEY = "equip"
        private const val EXTRA_KEY = "extra"
        private const val COST_KEY = "cost"
        private const val MAX_KEY = "max"
    }

    private val titleET by lazy { title_et }
    private val costEt by lazy { cost_et }
    private val equipEt by lazy { equip_tv }
    private val extraEt by lazy { extra_et }

    private val maxPplPicker by lazy {
        max_ppl_picker.apply {
            minValue = 0; maxValue = 1_000
        }
    }

    private val locationTV by lazy { location_tv }
    private val startDateTV by lazy { start_date_tv }
    private val endDateTV by lazy { end_date_tv }
    private val levelSpinner by lazy { level_spinner }

    private val eventImageView by lazy { event_img_view }
    private val progressDialog by lazy { ProgressDialog(this) }

    @Inject
    lateinit var viewModel: NewEditDataActivityVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_edit_data)

        (application as App).getAppComponent().inject(this)

        if (intent.hasExtra("dataInfo"))
            viewModel.dataInfo = intent!!.getParcelableExtra("dataInfo")

        select_loc_btn.setOnClickListener { startLocationActivity() }
        start_date_btn.setOnClickListener { showStartDatePicker() }
        end_date_btn.setOnClickListener { showEndDatePicker() }

        /*TODO check data info , from main?*/
        val dataInfo = viewModel.dataInfo
        if (dataInfo.type == DataType.EVENTS) {
            eventImageView.let {
                it.setOnClickListener { pickImage() }
                it.show()
            }
        }

        val id = dataInfo.id
        if (id == null) {
            val type = getString(dataInfo.type.singular)
            title = getString(R.string.newData, type)
            return
        }

        runOnBackground({ viewModel.getData(dataInfo.type, id) }) { data ->
            viewModel.data = data!!
            fillData(data)
            addListeners(data)
        }
    }

    private fun addListeners(data: BaseData) {
        titleET.addTextValidListener(TITLE_KEY) { data.title = it }
        equipEt.addTextValidListener(EQUIP_KEY) { data.equip = it }
        extraEt.addTextValidListener(EXTRA_KEY) { data.xtraNotes = it}
        costEt.addTextValidListener(COST_KEY) {
            it.toDoubleOrNull()?.let {
                data.cost = Money(it)
            }
        }

        maxPplPicker.setOnValueChangedListener { _, _, value ->
            if (form.validate().success()) {
                data.maxParticipants = value
            }
        }
    }

    private fun EditText.addTextValidListener(key: String, listener: (String) -> Unit) {
        addTextChangedListener {
            val res = form.validate()
            assert(res.success()) { "No success" }
            res[key]?.asString()?.let { listener(it) }
        }
    }

    private val form by lazy {
        form {
            input(titleET, TITLE_KEY) {
                isNotEmpty().description(R.string.fill_title)
                length().atLeast(3).description(R.string.at_least3)
            }
            input(costEt, COST_KEY) {
                isNotEmpty().description(R.string.fill_cost)
                isDecimal().atLeast(0.0).description(R.string.must_be_zero_or_more)
            }
            input(equipEt, EQUIP_KEY) {
                isNotEmpty().description(R.string.fill_equipment)
            }
            input(extraEt, EXTRA_KEY){}
            picker(maxPplPicker, MAX_KEY) {
                MaxNumberPickerAssertion()
            }
        }
    }

    private fun fillData(data: BaseData) = with(data) {
        title = data.title//activity title

        titleET.setText(title)
        costEt.setText(cost.amount.toString())

        locationTV.text = locationName

        levelSpinner.enumValue = level
        equipEt.setText(equip)
        maxPplPicker.value = maxParticipants

        viewModel.selectedStartDate = startDate
        viewModel.selectedEndDate = endDate

        extraEt.setText(xtraNotes)

        if (this is Event) {

            startDateTV.text = startDate.formatted()
            endDateTV.text = endDate.formatted()

            imageUrl?.let {
                Glide.with(eventImageView)
                    .load(it)
                    .transform(RoundedCorners(16))
                    .placeholder(R.drawable.img_placeholder)
                    .into(eventImageView)
            }
        } else {
            startDateTV.text = startDate.formatted(MEDIUM, SHORT)
            endDateTV.text = endDate.formatted(MEDIUM, SHORT)
        }
    }

    private fun startLocationActivity() {
        val intent = Intent(this, LocationPickerActivity::class.java)
        startActivityForResult(
            intent,
            RC_LOCATION
        )
    }

    private val myImagePicker by lazy { MyImagePicker(this) }

    private fun pickImage() = myImagePicker.show(this, viewModel.canRemoveImage)

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result code is RESULT_OK only if the user selects an Image
        myImagePicker.checkActivityResult(this, requestCode, resultCode, data)
        when (requestCode) {
            RC_LOCATION -> {
                if (resultCode == RESULT_OK && data != null) {
                    viewModel.selectedLocation =
                        data.getParcelableExtra<LocationResult>("location")!!
                            .also {
                                locationTV.text = it.address.getName()
                            }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.new_data_menu, menu)

        menu.findItem(R.id.nav_close_new).setIconTintCompat()

        form.submitWith(menu, R.id.nav_save) { result ->
            viewModel.data?.let { updateData(it) }
                ?: save(result)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_close_new -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateData(data: BaseData) {
        progressDialog.show()
        try {
            runOnBackground({
                when (data) {
                    is Lesson -> viewModel.updateLesson(data)
                    is Event -> viewModel.updateEvent(data)
                }
            }) { onSuccess() }
        } catch (e: Exception) {
            onFailed(e)
        }
    }


    private fun save(result: FormResult) {
        val data = createData(result) ?: return

        progressDialog.show()
        try {
            runOnBackground({
                viewModel.uploadData(data)
            }) { onSuccess() }
        } catch (e: Exception) {
            onFailed(e)
        }
    }

    private fun onFailed(e: Exception) {
        progressDialog.dismiss()
        alert(R.string.problemFound, e.localizedMessage)
            .setPositiveButton(R.string.ok) { _, _ ->
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            .show()

    }

    private fun onSuccess() {
        toast(R.string.success)

        val info = Intent().putExtra("dataInfo", viewModel.dataInfo)

        setResult(Activity.RESULT_OK, info)
        finish()
    }

    private fun createData(res: FormResult): BaseData? {
        val uid = viewModel.currentUser!!.id
        val title = res[TITLE_KEY]!!.asString()
        val cost = Money(res[COST_KEY]!!.asDouble()!!)

        val location = viewModel.selectedLocation ?: run {
            toast(R.string.select_location)
            return null
        }
        val coordinate = location.location.latLng
        val address = location.address
        val locationName = address.getName()
        val countryCode = address.countryCode

        val level = levelSpinner.enumValue!!
        val equip = res[EQUIP_KEY]!!.asString()
        val extra = extraEt.txt
        val maxPpl = res[MAX_KEY]!!.asInt()!!

        val startDate = viewModel.selectedStartDate ?: run {
            toast(R.string.select_start_date)
            return null
        }
        val endDate = viewModel.selectedEndDate ?: run {
            toast(R.string.select_end_date)
            return null
        }

        return when (viewModel.dataInfo.type) {
            DataType.LESSONS -> Lesson(
                title, cost, coordinate, locationName, countryCode,
                startDate, endDate, level, equip, extra, maxPpl, uid
            )
            DataType.EVENTS -> Event(
                title, cost, coordinate, locationName, countryCode,
                startDate, endDate, level, equip, extra,
                maxPpl, uid
            )
        }
    }

    private fun showStartDatePicker() {
        val minDate = Date().addMinuets(10)
        createDatePickerDialog(
            viewModel.selectedStartDate, minDate,
            BaseDataValidator::validateStartDate,
            {
                it?.let {
                    viewModel.selectedStartDate = it
                    startDateTV.text = it.formatted(MEDIUM, SHORT)
                }
            },
            getString(R.string.date_can_be_later),
            getString(R.string.select_start_date)
        )
    }

    private fun showEndDatePicker() {
        val startDate = viewModel.selectedStartDate ?: run {
            toast("Please select a start date")
            return
        }
        val validation: DateValidationPredicate = {
            BaseDataValidator.validateEndDate(it, startDate)
        }
        val type = getString(viewModel.dataInfo.type.singular)
        val endDate = viewModel.selectedEndDate
        val invalidMsg = getString(R.string.endDate_validation_invalid,type)
        val emptyMsg = getString(R.string.select_end_date)
        val minDate = startDate.addMinuets(30)
        createDatePickerDialog(
            endDate, minDate, validation,
            {
                it?.let {
                    viewModel.selectedEndDate = it
                    endDateTV.text = it.formatted(MEDIUM, SHORT)
                }
            },
            invalidMsg, emptyMsg
        )
    }

    private fun createDatePickerDialog(
        currentDate: Date?, minDate: Date,
        validation: DateValidationPredicate,
        listener: OnDateSet,
        invalidMsg: String, emptyMsg: String
    ) {
        val cal = currentDate?.cal() ?: minDate.cal()

        val onDateSet: (DatePicker, Int, Int, Int) -> Unit = { _, year, month, day ->
            val current = newDate(year, month, day)
            showTimePicker(current, minDate, invalidMsg, emptyMsg, validation, listener)
        }

        val year = cal.year
        val month = cal.month
        val day = cal.dayOfMonth

        DatePickerDialog(this, onDateSet, year, month, day).apply {
            datePicker.minDate = minDate.time
            datePicker.maxDate = minDate.addMonths(3).time
            show()
        }

    }

    private fun showTimePicker(
        current: Date, minDate: Date,
        invalidMsg: String,
        emptyMsg: String,
        validation: DateValidationPredicate,
        callback: OnDateSet
    ) {

        val cal = (if (current > minDate) current else minDate)
            .cal()

        val listener: (TimePicker, Int, Int) -> Unit = { _, hour, minute ->
            cal.hourOfDay = hour
            cal.minute = minute

            val result = validation(cal.time)
            if (result == VALID)
                callback(cal.time)
            else {
                when (result) {
                    EMPTY -> toast(emptyMsg)
                    INVALID -> toast(invalidMsg)
                }
                callback(null)
            }
        }
        TimePickerDialog(
            this, listener,
            cal.hourOfDay, cal.minute,
            DateFormat.is24HourFormat(this)
        )
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        myImagePicker.openCamIfPossible(this, requestCode, grantResults)
    }


    override fun onImageRemove() {
        eventImageView.setImageResource(R.drawable.img_placeholder)
        viewModel.result = null
    }

    override fun onSelectedImage(result: MyImagePicker.Result) {
        viewModel.result = result
        with(result) {
            (urls?.small ?: uri ?: bitmap)?.let {
                Glide.with(this@NewEditDataActivity).load(it)
                    .placeholder(R.drawable.img_placeholder)
                    .fitCenter()
                    .into(eventImageView)
            }
        }
    }
}
