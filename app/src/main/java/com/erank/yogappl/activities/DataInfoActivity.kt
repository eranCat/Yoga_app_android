package com.erank.yogappl.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.erank.yogappl.R
import com.erank.yogappl.activities.MainActivity.Companion.RC_EDIT
import com.erank.yogappl.models.BaseData
import com.erank.yogappl.models.DataInfo
import com.erank.yogappl.models.Event
import com.erank.yogappl.models.Lesson
import com.erank.yogappl.utils.data_source.DataSource
import com.erank.yogappl.utils.extensions.alert
import com.erank.yogappl.utils.extensions.formatted
import com.erank.yogappl.utils.extensions.setIconTintCompat
import com.erank.yogappl.utils.extensions.toast
import com.erank.yogappl.utils.helpers.LocationHelper
import com.erank.yogappl.utils.helpers.RemindersAdapter
import com.erank.yogappl.utils.interfaces.TaskCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_data_info.*
import kotlinx.android.synthetic.main.profile_image.*
import kotlinx.android.synthetic.main.progress_layout.*
import java.text.DateFormat.MEDIUM
import java.text.DateFormat.SHORT

class DataInfoActivity : AppCompatActivity(), TaskCallback<Boolean, Exception> {

    private lateinit var currentData: BaseData
    private lateinit var dataInfo: DataInfo

    private val progressLayout by lazy { progress_layout }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_info)

        dataInfo = intent!!.getParcelableExtra("dataInfo")
        val dataType = dataInfo.dataType
        val sourceType = dataInfo.sourceType
        val pos = dataInfo.position!!

        DataSource.getData(dataType, sourceType, pos)?.let {
            currentData = it
            fillData(it)
            if (it is Event) {
                loadEventImage(it)
            }
        } ?: finish()
    }

    private fun loadEventImage(event: Event) {
        event.imageUrl?.let {

            event_img.visibility = View.VISIBLE

            Glide.with(this@DataInfoActivity)
                .load(it)
                .placeholder(R.drawable.close_up_of_leaf)
                .fitCenter()
                .into(event_img)

        }
    }

    private fun fillData(data: BaseData) {
        title = data.title

        data.apply {

            DataSource.getUser(uid)?.let {

                Glide.with(this@DataInfoActivity)
                    .load(it.profileImageUrl)
                    .placeholder(R.drawable.yoga_model)
                    .circleCrop()
                    .into(profile_Img)

                teacher_name.text = it.name
                teacher_about.text = it.about
            }

            title_tv.text = title
            cost_tv.text = cost.toString()

            val startDateStr = startDate.formatted(MEDIUM, SHORT)
            val endDateStr = endDate.formatted(MEDIUM, SHORT)
            date_btn.text = getString(R.string.range, startDateStr, endDateStr)

            current_signed.text = signed.size.toString()
            total_signed.text = maxParticipants.toString()

            min_age_tv.text = (if (minAge < 0) 0 else minAge).toString()
            max_age_tv.text = maxAge.toString()

            equip_et.text = equip
            if (extraNotes != null && extraNotes!!.isNotEmpty()) {
                extras_tv.text = extraNotes
            } else {
                extra_notes_title.visibility = View.GONE
                extras_tv.visibility = View.GONE
            }
        }

        location_btn.apply {
            text = data.locationName

            setOnClickListener { openLocation(data.location) }
        }
    }

    private lateinit var toggleSignNav: MenuItem
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.data_menu, menu)

        val uid = DataSource.currentUser!!.id
        toggleSignNav = menu.findItem(R.id.nav_toggle_sign)
        if (currentData.signed.contains(uid)){
            toggleSignNav.title = "Sign out"
        }

        menu.findItem(R.id.nav_share).setIconTintCompat()

        if (currentData.uid == uid) {
            toggleSignNav.isVisible = false
            menu.findItem(R.id.nav_edit).isVisible = true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_toggle_sign -> {
                item.isEnabled = false
                toggleSign()
                true
            }
            R.id.nav_edit -> {
                val intent = Intent(this, NewEditDataActivity::class.java)
                    .putExtra("dataInfo", dataInfo)
                startActivityForResult(intent, RC_EDIT)
                true
            }
            R.id.nav_share -> {
                share()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_EDIT -> if (resultCode == RESULT_OK)
                fillData(currentData)
        }
    }

    private fun toggleSign() {
        progressLayout.visibility = View.VISIBLE

        when (val it = currentData) {
            is Lesson -> DataSource.toggleSignToLesson(it, this)
            is Event -> DataSource.toggleSignToEvent(it, this)
        }
    }

    private fun share() {
        toast("share")
        //TODO share
    }


    private fun openLocation(location: LatLng) {
        val locationIntent = LocationHelper.getLocationIntent(packageManager, location)

        locationIntent?.let {
            startActivity(it)
        }
    }

    private val remindersAdapter by lazy { RemindersAdapter(currentData) }

    override fun onSuccess(result: Boolean?) {
        progressLayout.visibility = View.GONE
        toggleSignNav.isEnabled = true

        result!!
        val inout = if (result) "out" else "in"
        toggleSignNav.title = "Sign $inout"

        with(remindersAdapter) {
            this@DataInfoActivity.run {
                if (result)
                    showDialog(this)
                else
                    removeReminder(this, currentData)
            }
        }
    }

    override fun onFailure(error: Exception) {
        progressLayout.visibility = View.GONE
        toggleSignNav.isEnabled = true
        //in or out
        alert("Failed signing", error.localizedMessage)
            .setPositiveButton("ok", null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        remindersAdapter
            .tryAgainIfAvailable(this, permissions, grantResults)
    }
}
