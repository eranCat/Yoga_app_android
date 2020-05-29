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
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.erank.yogappl.R
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.enums.TextFieldValidStates
import com.erank.yogappl.data.enums.TextFieldValidStates.*
import com.erank.yogappl.data.models.*
import com.erank.yogappl.ui.activities.location.LocationPickerActivity
import com.erank.yogappl.ui.custom_views.ProgressDialog
import com.erank.yogappl.utils.App
import com.erank.yogappl.utils.DateValidationPredicate
import com.erank.yogappl.utils.OnDateSet
import com.erank.yogappl.utils.extensions.*
import com.erank.yogappl.utils.helpers.BaseDataValidator
import com.erank.yogappl.utils.helpers.MyImagePicker
import com.erank.yogappl.utils.interfaces.ImagePickerCallback
import com.erank.yogappl.utils.runOnBackground
import com.unsplash.pickerandroid.photopicker.data.UnsplashUrls
import kotlinx.android.synthetic.main.activity_new_edit_data.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
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

    private lateinit var validator: BaseDataValidator

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
            it?.let {
                fillData(it)
                initValidator(VALID)
                viewModel.data = it
                title = it.title
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
                    viewModel.selectedLocation = data.getParcelableExtra("location")

                    viewModel.selectedLocation?.address?.let {
                        locationTV.text = it.longName
                    }
                }
                if (validator.validateLocation(viewModel.selectedLocation) != VALID)
                    toast("no location picked")
            }
        }
    }

    private fun initValidator(state: TextFieldValidStates = INVALID) {
        validator = BaseDataValidator(state)
        val v = validator//shorter name

        with(titleET) {
            setTextChangedListener {
                error = v.validateTitle(it).errorMsg
            }
        }

        with(costEt) {
            setTextChangedListener {
                error = v.validateCost(it).errorMsg
            }
        }

        with(equipEt) {
            setTextChangedListener {
                error = v.validateEquipment(it).errorMsg
            }
        }

        with(levelSpinner) {
            setOnItemSelectedListener {
                if (v.validateLevel(it) != VALID) {
                    toast("Select level")
                }
            }
        }

        maxPplPicker.setOnValueChangedListener { _, _, count ->
            if (v.validateMinMaxPPL(count) != VALID)
                toast("Pick a number")
        }

        start_date_btn.setOnClickListener { showStartDatePicker() }
        start_date_bar.setOnClickListener { showStartDatePicker() }

        end_date_btn.setOnClickListener { showEndDatePicker() }
        end_date_bar.setOnClickListener { showEndDatePicker() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.new_data_menu, menu)

        menu.findItem(R.id.nav_close_new).setIconTintCompat()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_save -> {
                save(item)
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

    private fun save(item: MenuItem) {
        if (validator.isDataValid.not()) {
            toast("make sure everything is filled correctly")
            return
        }

        item.isEnabled = false
        progressDialog.show()

        viewModel.data?.let {
            createData(it)

            runOnBackground({
                try {
                    when (it) {
                        is Lesson -> viewModel.updateLesson(it)
                        is Event -> viewModel.updateEvent(it)
                    }
                } catch (e: Exception) {
                    withContext(Main) { onFailed(e) }
                }
            }, this@NewEditDataActivity::onSuccess)

        } ?: runOnBackground({
            try {
                viewModel.uploadData(createData())
            } catch (e: Exception) {
                withContext(Main) { onFailed(e) }
            }
        }, this@NewEditDataActivity::onSuccess)
    }

    private fun onFailed(e: Exception) {
        progressDialog.dismiss()
        alert("Problem found", e.localizedMessage)
            .setPositiveButton("ok", null)
            .show()

        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun onSuccess() {
        toast("success!")

        val info = Intent().putExtra("dataInfo", viewModel.dataInfo)

        setResult(Activity.RESULT_OK, info)
        finish()
    }

    private fun createData(data: BaseData? = null): BaseData {
        val uid = viewModel.currentUser!!.id
        val title = titleET.text.toString()
        val cost = Money(costEt.text.toString().toDouble())

        val selectedLocation = viewModel.selectedLocation!!
        val coordinate = selectedLocation.location.latLng
        val address = selectedLocation.address
        val locationName = address.streetName ?: address.localName
        ?: address.longName.ifEmpty { "Location" }
        val countryCode = address.countryCode
        val level = levelSpinner.enumValue!!
        val equip = equipEt.text.toString()
        val extra = extraEt.text.toString()
        val maxPpl = maxPplPicker.value

        val startDate = viewModel.selectedStartDate!!
        val endDate = viewModel.selectedEndDate!!

        val imageUrl = viewModel.result?.urls?.small

        data?.let {

            it.title = title
            it.cost = cost
            it.location = coordinate
            it.locationName = locationName
            it.countryCode = countryCode
            it.level = level
            it.equip = equip
            it.extraNotes = extra
            it.maxParticipants = maxPpl
            it.startDate = startDate
            it.endDate = endDate

            (it as? Event)?.imageUrl = imageUrl

            return it
        }

        return when (viewModel.dataInfo.type) {
            DataType.LESSONS -> Lesson(
                title, cost, coordinate, locationName, countryCode,
                startDate, endDate, level, equip, extra, maxPpl, uid
            )
            DataType.EVENTS -> Event(
                title, cost, coordinate, locationName, countryCode,
                startDate, endDate, level, equip, extra,
                maxPpl, uid, imageUrl
            )
        }
    }

    private fun showStartDatePicker() {
        createDatePickerDialog(
            viewModel.selectedStartDate, null,
            validator::validateStartDate,
            {
                it?.let {
                    viewModel.selectedStartDate = it
                    startDateTV.text = viewModel.selectedStartDate!!.formatted(MEDIUM, SHORT)
                }
            },
            "date can be today or beyond. time needs to be in the future",
            "please select a start date"
        )
    }

    private fun showEndDatePicker() {
        val startDate = viewModel.selectedStartDate ?: run {
            toast("Please select a start date")
            return
        }
        val validation: DateValidationPredicate = {
            validator.validateEndDate(it, startDate)
        }
        createDatePickerDialog(
            viewModel.selectedEndDate, startDate, validation, {
                it?.let {
                    viewModel.selectedEndDate = it
                    endDateTV.text = viewModel.selectedEndDate!!.formatted(MEDIUM, SHORT)
                }
            }, "date needs to be same as start date or after.\n" +
                    "and min time of ${viewModel.dataInfo.type.singular} is 30 min",
            "please select an end date"
        )
    }

    private fun createDatePickerDialog(
        currentDate: Date?,
        minDate: Date? = null,
        validation: DateValidationPredicate,
        listener: OnDateSet,
        invalidMsg: String, emptyMsg: String
    ) {
        val cal = Calendar.getInstance()

        if (currentDate != null)
            cal.time = currentDate

        val onDateSet: (DatePicker, Int, Int, Int) -> Unit = { _, year, month, day ->
            val current = newDate(year, month, day)
            showTimePicker(current, invalidMsg, emptyMsg, validation, listener)
        }

        val year = cal.year
        val month = cal.month
        val day = cal.dayOfMonth

        DatePickerDialog(this, onDateSet, year, month, day)
            .apply {
                //                datePicker.spinnersShown = false
//                datePicker.calendarViewShown = true
                datePicker.minDate = (minDate ?: Date()).time
                datePicker.maxDate = Date().addMonths(3).time

            }.show()

    }

    private fun showTimePicker(
        current: Date,
        invalidMsg: String,
        emptyMsg: String,
        validation: DateValidationPredicate,
        callback: OnDateSet
    ) {

        val cal = Calendar.getInstance().apply { time = current }

        val is24HourFormat = DateFormat.is24HourFormat(this)
        TimePickerDialog(
            this, { _, hour, min ->
                cal.setHour(is24HourFormat, hour)
                cal.minute = min

                when (validation.invoke(cal.time)) {
                    EMPTY -> toast(emptyMsg)
                    INVALID -> toast(invalidMsg)
                    VALID -> {
                        callback.invoke(cal.time)
                        return@TimePickerDialog
                    }
                }

                callback.invoke(null)

            }, cal.getHour(is24HourFormat), cal.minute, is24HourFormat
        ).show()
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
