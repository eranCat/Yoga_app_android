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

        private val teacherImgView by lazy { itemView.profile_Img }
        private val teacherNameTv by lazy { itemView.teacherNameTV }

        private val kindTv by lazy { itemView.kindTV }
        private val placeTv by lazy { itemView.placeTV }
        private val timeTv by lazy { itemView.timeTV }

        private val dropDownSection by lazy { itemView.drop_down }
        private val dropDownBtn by lazy { itemView.dropdown_btn }
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
            item as Lesson

            users[item.uid]?.let {
                Glide.with(teacherImgView)
                    .load(it.profileImageUrl)
                    .placeholder(R.drawable.yoga_model)
                    .fallback(R.drawable.yoga_model)
                    .circleCrop()
                    .into(teacherImgView)

                teacherNameTv.text = it.name
            }

            if (!isEditable) {
                signBtn.isEnabled = !userUploads.contains(item.id)
            }

            with(item) {
                kindTv.text = title
                placeTv.text = locationName
                timeTv.text = startDate.relativeTimeString(itemView.context)
            }

            setOnClickListeners(item)
        }

        override fun setOnClickListeners(item: Lesson) {
            val id = item.id

            itemView.setOnClickListener {
                callback?.onItemSelected(item)
            }

            dropDownBtn.setOnClickListener {

                val isVisible = toggles[id] ?: false
                it.toggleRotation(isVisible)
                dropDownSection.toggleSlide(isVisible)

                toggles[id] = !isVisible
            }

            if (isEditable) {
                editBtn.setOnClickListener {
                    callback?.onEditAction(item)
                }
                return
            }

            val action =
                if (signed.contains(item.id)) R.string.sign_out
                else R.string.sign_in

            signBtn.text = itemView.context.getString(action)

            signBtn.setOnClickListener {
                callback?.onSignAction(item)
            }

        }
    }

}
