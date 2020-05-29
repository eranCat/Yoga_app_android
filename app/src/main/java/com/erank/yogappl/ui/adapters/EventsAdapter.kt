package com.erank.yogappl.ui.adapters


import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.erank.yogappl.R
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.models.Event
import com.erank.yogappl.utils.extensions.equalDate
import com.erank.yogappl.utils.extensions.equalTime
import com.erank.yogappl.utils.extensions.toggleRotation
import com.erank.yogappl.utils.extensions.toggleSlide
import kotlinx.android.synthetic.main.drop_down_btn.view.*
import kotlinx.android.synthetic.main.dropdown_menu.view.*
import kotlinx.android.synthetic.main.event_item.view.*
import java.text.DateFormat.MEDIUM
import java.text.DateFormat.SHORT
import java.text.SimpleDateFormat


class EventsAdapter(
    isEditable: Boolean,
    private val userUploads: MutableList<String>,
    private val signed: MutableList<String>
) :
    DataListAdapter<Event, EventsAdapter.EventVH>(isEditable) {

    override val dataType = DataType.EVENTS

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EventVH(parent)

    inner class EventVH(parent: ViewGroup) : DataVH<Event>(parent, R.layout.event_item) {

        private val eventImage by lazy { itemView.event_img_view }
        private val eventName by lazy { itemView.nameTv }
        private val eventDate by lazy { itemView.dateTV }
        private val eventTime by lazy { itemView.timeTV }
        private val dropDownBtn by lazy { itemView.dropdown_btn }
        private val dropDownSection by lazy { itemView.drop_down }
        private val editBtn by lazy { itemView.edit_btn }
        private val deleteBtn by lazy { itemView.delete_btn }
        private val signBtn by lazy { itemView.sign_btn }

        init {
            val visibility = if (isEditable) VISIBLE else GONE
            editBtn.visibility = visibility
            deleteBtn.visibility = visibility

            signBtn.visibility = if (isEditable) GONE else VISIBLE
        }

        override fun bind(item: Event) {
            if (item.imageUrl != null) {

                eventImage.visibility = VISIBLE

                Glide.with(eventImage)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.img_placeholder)
                    .fitCenter()
                    .into(eventImage)
            } else
                eventImage.visibility = GONE


            if (!isEditable) {
                signBtn.isEnabled = !userUploads.contains(item.id)
            }

            eventName.text = item.title

            val ctx = eventDate.context

            val dateFormatter = SimpleDateFormat.getDateInstance(MEDIUM)
            val start = item.startDate
            val end = item.endDate

            if (start.equalDate(end)) {
                eventDate.text = dateFormatter.format(start)
            } else {
                val startDate = dateFormatter.format(start)
                val endDate = dateFormatter.format(end)
                eventDate.text = ctx.getString(R.string.range, startDate, endDate)
            }

            val timeFormatter = SimpleDateFormat.getTimeInstance(SHORT)
            if (start.equalTime(end)) {
                eventTime.text = timeFormatter.format(start)
            } else {
                val startTime = timeFormatter.format(start)
                val endTime = timeFormatter.format(end)
                eventTime.text = ctx.getString(R.string.range, startTime, endTime)
            }
            setOnClickListeners(item)
        }

        override fun setOnClickListeners(event: Event) {
            val id = event.id

            itemView.setOnClickListener {
                callback?.onItemSelected(event)
            }

            dropDownBtn.setOnClickListener {
                val isVisible = toggles[id] ?: false
                it.toggleRotation(isVisible)
                dropDownSection.toggleSlide(isVisible)

                toggles[id] = !isVisible
            }

            if (isEditable) {
                editBtn.setOnClickListener { callback?.onEditAction(event) }
                deleteBtn.setOnClickListener { callback?.onDeleteAction(event) }
            } else {
                val isSigned = signed.contains(event.id)
                val action = if (isSigned) "out" else "in"
                signBtn.text = "Sign $action"
                signBtn.setOnClickListener {
                    callback?.onSignAction(event)
                }
            }
        }

    }
}
