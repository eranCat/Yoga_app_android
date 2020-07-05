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

        init {
            with(itemView) {
                if (isEditable) {
                    edit_btn.show()
                    sign_btn.hide()
                } else {
                    edit_btn.hide()
                    sign_btn.show()
                }
            }
        }

        override fun bind(item: Any) =with(itemView){
            item as Event
            if (item.imageUrl != null) {

                event_img_view.show()

                Glide.with(event_img_view)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.img_placeholder)
                    .fitCenter()
                    .into(event_img_view)
            } else {
                event_img_view.hide()
            }


            if (!isEditable) {
                sign_btn.isEnabled = !userUploads.contains(item.id)
            }

            nameTv.text = item.title

            val ctx = itemView.context

            val dateFormatter = SimpleDateFormat.getDateInstance(MEDIUM)
            val start = item.startDate
            val end = item.endDate

            if (start.equalDate(end)) {
                timeTV.text = dateFormatter.format(start)
            } else {
                val startDate = dateFormatter.format(start)
                val endDate = dateFormatter.format(end)
                dateTV.text = ctx.getString(R.string.range, startDate, endDate)
            }

            val timeFormatter = SimpleDateFormat.getTimeInstance(SHORT)
            if (start.equalTime(end)) {
                timeTV.text = timeFormatter.format(start)
            } else {
                val startTime = timeFormatter.format(start)
                val endTime = timeFormatter.format(end)
                timeTV.text = ctx.getString(R.string.range, startTime, endTime)
            }
            setOnClickListeners(item)
        }

        override fun setOnClickListeners(event: Event) {
            val id = event.id

            itemView.setOnClickListener {
                callback?.onItemSelected(event)
            }

            itemView.dropdown_btn.setOnClickListener {
                val isVisible = toggles[id] ?: false
                it.toggleRotation(isVisible)
                itemView.drop_down.toggleSlide(isVisible)

                toggles[id] = !isVisible
            }

            if (isEditable) {
                itemView.edit_btn.setOnClickListener { callback?.onEditAction(event) }
                return
            }

            val isSigned = signed.contains(event.id)
            val action =
                if (isSigned) R.string.sign_out
                else R.string.sign_out
            with(itemView.sign_btn) {
                text = context.getString(action)
                setOnClickListener {
                    callback?.onSignAction(event)
                }
            }
        }

    }
}
