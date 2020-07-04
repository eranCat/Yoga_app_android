package com.erank.yogappl.ui.adapters


import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.erank.yogappl.R
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.models.Event
import com.erank.yogappl.utils.extensions.*
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
    DataListAdapter<Event>(isEditable) {

    override val dataType = DataType.EVENTS

    override fun createDataViewHolder(parent: ViewGroup) = EventVH(parent)

    inner class EventVH(parent: ViewGroup) : DataVH<Event>(parent, R.layout.event_item) {

        private val eventImage by lazy { itemView.event_img_view }
        private val eventName by lazy { itemView.nameTv }
        private val eventDate by lazy { itemView.dateTV }
        private val eventTime by lazy { itemView.timeTV }
        private val dropDownBtn by lazy { itemView.dropdown_btn }
        private val dropDownSection by lazy { itemView.drop_down }
        private val editBtn by lazy { itemView.edit_btn }
        private val signBtn by lazy { itemView.sign_btn }

        init {
            if (isEditable) {
                editBtn.show()
                signBtn.hide()
            } else {
                editBtn.hide()
                signBtn.show()
            }
        }

        override fun bind(item: Any) {
            item as Event
            if (item.imageUrl != null) {

                eventImage.show()

                Glide.with(eventImage)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.img_placeholder)
                    .fitCenter()
                    .into(eventImage)
            } else {
                eventImage.hide()
            }


            if (!isEditable) {
                signBtn.isEnabled = !userUploads.contains(item.id)
            }

            eventName.text = item.title

            val ctx = itemView.context

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
                return
            }

            val isSigned = signed.contains(event.id)
            val action =
                if (isSigned) R.string.sign_out
                else R.string.sign_out
            signBtn.text = itemView.context.getString(action)
            signBtn.setOnClickListener {
                callback?.onSignAction(event)
            }
        }

    }
}
