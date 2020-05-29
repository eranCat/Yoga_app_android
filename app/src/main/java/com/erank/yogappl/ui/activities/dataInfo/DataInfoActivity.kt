package com.erank.yogappl.ui.activities.dataInfo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.erank.yogappl.R
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.data.models.DataInfo
import com.erank.yogappl.data.models.Event
import com.erank.yogappl.data.models.Lesson
import com.erank.yogappl.ui.activities.newEditData.NewEditDataActivity
import com.erank.yogappl.utils.extensions.*
import com.erank.yogappl.utils.helpers.RemindersAdapter
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_data_info.*
import kotlinx.android.synthetic.main.profile_image.*
import kotlinx.android.synthetic.main.progress_layout.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat.MEDIUM
import java.text.DateFormat.SHORT
import javax.inject.Inject

class DataInfoActivity : AppCompatActivity() {

    private val RC_EDIT: Int = 2
    private var currentData: BaseData? = null
    private lateinit var dataInfo: DataInfo

    private val progressLayout by lazy { progress_layout }

    @Inject
    lateinit var viewModel: DataInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_info)


        dataInfo = intent!!.getParcelableExtra("dataInfo")
        val dataType = dataInfo.type

        GlobalScope.launch(IO) {
            val data = viewModel.getData(dataType, dataInfo.id!!)
            withContext(Main) {
                data ?: run {
                    finish()
                    return@withContext
                }
                currentData = data
                fillData(data)
                (data as? Event)?.let { loadEventImage(it) }
            }
        }
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
        GlobalScope.launch(IO) {
            val user = viewModel.getUser(data.uid)
            withContext(Main) {
                user?.let {
                    Glide.with(this@DataInfoActivity)
                        .load(it.profileImageUrl)
                        .placeholder(R.drawable.yoga_model)
                        .circleCrop()
                        .into(profile_Img)

                    teacher_name.text = it.name
                    teacher_about.text = it.about
                }
            }
        }

        title_tv.text = title
        cost_tv.text = data.cost.toString()

        val startDateStr = data.startDate.formatted(MEDIUM, SHORT)
        val endDateStr = data.endDate.formatted(MEDIUM, SHORT)
        date_btn.text = getString(R.string.range, startDateStr, endDateStr)

        current_signed.text = data.signed.size.toString()
        total_signed.text = data.maxParticipants.toString()

        min_age_tv.text =
            (if (data.minAge < 0) 0
            else data.minAge).toString()

        max_age_tv.text = data.maxAge.toString()

        equip_et.text = data.equip
        val extraNotes = data.extraNotes
        if (extraNotes == null || extraNotes.isEmpty()) {
            extra_notes_title.hide()
            extras_tv.hide()
        } else {
            extras_tv.text = extraNotes
        }

        location_btn.apply {
            text = data.locationName
            setOnClickListener { openLocation(data.location) }
        }
    }

    private lateinit var toggleSignNav: MenuItem
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.data_menu, menu)
        menu.findItem(R.id.nav_share).setIconTintCompat()

        toggleSignNav = menu.findItem(R.id.nav_toggle_sign)


        val uid = viewModel.currentUser!!.id
        currentData?.let {
            if (it.signed.contains(uid)) {
                toggleSignNav.title = "Sign out"
            }

            if (it.uid == uid) {
                toggleSignNav.isVisible = false
                menu.findItem(R.id.nav_edit).isVisible = true
            }
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
                currentData?.let { fillData(it) }
        }
    }

    private fun toggleSign() {
        progressLayout.show()

        GlobalScope.launch(IO) {
            try {
                val result = when (val it = currentData) {
                    is Lesson -> viewModel.toggleSignToLesson(it)
                    is Event -> viewModel.toggleSignToEvent(it)
                    else -> false
                }
                withContext(Main) { onSignToggled(result) }
            } catch (e: Exception) {
                onFailedSign(e)
            }
        }
    }

    private fun share() {
        toast("share")
        //TODO share
    }


    private fun openLocation(location: LatLng) =
        viewModel.getLocationIntent(location)?.let {
            startActivity(it)
        }

    private var remindersAdapter: RemindersAdapter<BaseData>? = null

    private fun onSignToggled(result: Boolean) {
        progressLayout.hide()
        toggleSignNav.isEnabled = true

        toggleSignNav.title = "Sign ${if (result) "out" else "in"}"

        val data = currentData ?: return

        if (remindersAdapter == null)
            remindersAdapter = RemindersAdapter(data)

        remindersAdapter!!.let {
            if (result) it.showDialog(this)
            else currentData?.let { data ->
                it.removeReminder(data)
            }
        }
    }

    fun onFailedSign(error: Exception) {
        progressLayout.hide()
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
            ?.tryAgainIfAvailable(this, permissions, grantResults)
    }
}
