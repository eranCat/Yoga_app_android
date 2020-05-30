package com.erank.yogappl.ui.activities.newEditData

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.vvalidator.form
import com.afollestad.vvalidator.form.Form
import com.afollestad.vvalidator.form.FormResult
import com.bumptech.glide.Glide
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
import com.unsplash.pickerandroid.photopicker.data.UnsplashUrls
import kotlinx.android.synthetic.main.activity_new_edit_data.*
import java.text.DateFormat.MEDIUM
import java.text.DateFormat.SHORT
import java.util.*
import javax.inject.Inject

class NewEditDataActivity : AppCompatActivity(), ImagePickerCallback {

    companion object {
        const val RC_LOCATION = 11
        private val TAG = NewEditDataActivity::class.java.name
    }

    private val titleET by lazy { title_et }
    private val costEt by lazy { cost_et }
    private val equipEt by lazy { equip_et }
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
        location_bar.setOnClickListener { startLocationActivity() }

        start_date_btn.setOnClickListener { showStartDatePicker() }
        start_date_bar.setOnClickListener { showStartDatePicker() }

        end_date_btn.setOnClickListener { showEndDatePicker() }
        end_date_bar.setOnClickListener { showEndDatePicker() }

        /*TODO check data info , from main?*/
        val dataInfo = viewModel.dataInfo
        if (dataInfo.type == DataType.EVENTS) {
            eventImageView.let {
                it.setOnClickListener { pickImage() }
                it.visibility = View.VISIBLE
            }
        }

        val id = dataInfo.id
        if (id == null) {
            initValidator()
            title = "New ${dataInfo.type.singular}"
            return
        }

        runOnBackground({ viewModel.getData(dataInfo.type, id) }) { it ->
            it?.let { data ->
                fillData(data)
                initValidatorForUpdate(data)
                viewModel.data = data
                title = data.title
            }
        }
    }

    private lateinit var form: Form
    private fun initValidator() {
        form = form {
            input(titleET, "title") {
                isNotEmpty().description("Please fill a title")
                length().atLeast(3).description("Must be at least 3 characters long")
            }
            input(costEt, "cost") {
                isNotEmpty().description("Please fill a cost")
                isNumber().atLeast(0).description("must be a positive number or 0")
            }
            input(equipEt, "equip") {
                isNotEmpty().description("Please fill a equipment")
            }
            picker(maxPplPicker, "max") {
                MaxNumberPickerAssertion()
            }
        }
    }

    private fun initValidatorForUpdate(data: BaseData) {
        form = form {
            input(titleET) {
                isNotEmpty().description("Please fill a title")
                length().atLeast(3).description("Must be at least 3 characters long")
                data.title = titleET.txt
            }
            input(costEt) {
                isNotEmpty().description("Please fill a cost")
                isNumber().atLeast(0).description("must be a positive number or 0")
                data.cost = Money(costEt.txt.toDouble())
            }
            input(equipEt) {
                isNotEmpty().description("Please fill a equipment")
                data.equip = equipEt.txt
            }
            spinner(levelSpinner) {
                data.level = levelSpinner.enumValue!!
            }
            picker(maxPplPicker) {
                MaxNumberPickerAssertion()
                data.maxParticipants = maxPplPicker.value
            }
        }
    }

    private fun fillData(data: BaseData) = with(data) {
        titleET.setText(title)
        costEt.setText(cost.amount.toString())

        val address = Address(locationName, countryCode)
        val loc = Position(location)
        viewModel.selectedLocation = LocationResult(address, loc)
        locationTV.text = locationName

        levelSpinner.enumValue = level
        equipEt.setText(equip)
        maxPplPicker.value = maxParticipants

        viewModel.selectedStartDate = startDate
        viewModel.selectedEndDate = endDate

        extraEt.setText(extraNotes)

        if (this is Event) {

            startDateTV.text = startDate.formatted()
            endDateTV.text = endDate.formatted()

            imageUrl?.let {
                val unsplashUrls = UnsplashUrls(null, it, null, null, null, null, null)
                viewModel.result = MyImagePicker.Result(urls = unsplashUrls)
                Glide.with(eventImageView).load(it)
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

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_save -> {
                viewModel.data?.let {
                    updateData(it, item)
                } ?: save(item)
                true
            }
            R.id.nav_close_new -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateData(data: BaseData, item: MenuItem) {
        item.isEnabled = false
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


    private fun save(item: MenuItem) {
        val formResult = form.validate()
        if (!formResult.success()) {
            toast("make sure everything is filled correctly")
            return
        }
        val data = createData(formResult) ?: return

        item.isEnabled = false
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
        alert("Problem found", e.localizedMessage)
            .setPositiveButton("ok") { _, _ ->
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            .show()

    }

    private fun onSuccess() {
        toast("success!")

        val info = Intent().putExtra("dataInfo", viewModel.dataInfo)

        setResult(Activity.RESULT_OK, info)
        finish()
    }

    private fun createData(res: FormResult): BaseData? {
        val uid = viewModel.currentUser!!.id
        val title = res["title"]!!.asString()
        val cost = Money(res["cost"]!!.asDouble()!!)

        val location = viewModel.selectedLocation ?: run {
            toast("Please select a location")
            return null
        }
        val coordinate = location.location.latLng
        val address = location.address
        val locationName = address.getName()
        val countryCode = address.countryCode

        val level = levelSpinner.enumValue!!//TODO can use custom field
        val equip = res["equip"]!!.asString()
        val extra = extraEt.txt
        val maxPpl = res["max"]!!.asInt()!!

        val startDate = viewModel.selectedStartDate ?: run {
            toast("Please select a start date")
            return null
        }
        val endDate = viewModel.selectedEndDate ?: run {
            toast("Please select an end date")
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
            "date can be later",
            "please select a start date"
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
        val type = viewModel.dataInfo.type.singular
        val endDate = viewModel.selectedEndDate
        val invalidMsg = "date needs to be same as start date or after.\n" +
                "and min time of $type is 30 min"
        val emptyMsg = "please select an end date"
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
