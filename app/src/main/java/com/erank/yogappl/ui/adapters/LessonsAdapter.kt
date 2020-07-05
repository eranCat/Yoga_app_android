package com.erank.yogappl.ui.adapters

import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.erank.yogappl.R
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.models.Lesson
import com.erank.yogappl.data.models.PreviewUser
import com.erank.yogappl.utils.extensions.*
import kotlinx.android.synthetic.main.drop_down_btn.view.*
import kotlinx.android.synthetic.main.dropdown_menu.view.*
import kotlinx.android.synthetic.main.lesson_item.view.*
import kotlinx.android.synthetic.main.profile_image.view.*


class LessonsAdapter(
    isEditable: Boolean,
    private val userUploads: List<String>,
    private val signed: MutableList<String>
) :
    DataListAdapter<Lesson>(isEditable) {

    private var users: Map<String, PreviewUser> = emptyMap()
    override val dataType = DataType.LESSONS

    override fun createDataViewHolder(parent: ViewGroup) = LessonVH(parent)

    fun setUsers(users: Map<String, PreviewUser>) {
        this.users = users
    }

    inner class LessonVH(parent: ViewGroup) :
        DataVH<Lesson>(parent, R.layout.lesson_item) {

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

        override fun bind(item: Any) =with(itemView) {
            item as Lesson

            users[item.uid]?.let {
                Glide.with(profile_Img)
                    .load(it.profileImageUrl)
                    .placeholder(R.drawable.yoga_model)
                    .fallback(R.drawable.yoga_model)
                    .circleCrop()
                    .into(profile_Img)

                teacherNameTV.text = it.name
            }

            if (!isEditable) {
                sign_btn.isEnabled = !userUploads.contains(item.id)
            }

            with(item) {
                kindTV.text = title
                placeTV.text = locationName
                timeTV.text = startDate.relativeTimeString(itemView.context)
            }

            setOnClickListeners(item)
        }

        override fun setOnClickListeners(item: Lesson) {
            val id = item.id

            itemView.setOnClickListener {
                callback?.onItemSelected(item)
            }

            itemView.dropdown_btn.setOnClickListener {

                val isVisible = toggles[id] ?: false
                it.toggleRotation(isVisible)
                itemView.drop_down.toggleSlide(isVisible)

                toggles[id] = !isVisible
            }

            if (isEditable) {
                itemView.edit_btn.setOnClickListener {
                    callback?.onEditAction(item)
                }
                return
            }

            val action =
                if (signed.contains(item.id)) R.string.sign_out
                else R.string.sign_in

            with(itemView.sign_btn) {
                text = context.getString(action)
                setOnClickListener {
                    callback?.onSignAction(item)
                }
            }
        }
    }

}
