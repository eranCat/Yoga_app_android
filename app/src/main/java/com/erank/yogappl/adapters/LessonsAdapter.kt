package com.erank.yogappl.adapters

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.erank.yogappl.R
import com.erank.yogappl.models.Lesson
import com.erank.yogappl.models.Teacher
import com.erank.yogappl.utils.data_source.DataSource
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.extensions.relativeTimeString
import com.erank.yogappl.utils.extensions.toggleRotation
import com.erank.yogappl.utils.extensions.toggleSlide
import kotlinx.android.synthetic.main.drop_down_btn.view.*
import kotlinx.android.synthetic.main.dropdown_menu.view.*
import kotlinx.android.synthetic.main.lesson_item.view.*
import kotlinx.android.synthetic.main.profile_image.view.*


class LessonsAdapter(
    list: MutableList<Lesson>,
    isEditable: Boolean
) :
    DataListAdapter<Lesson, LessonsAdapter.LessonVH>(list, isEditable) {

    override val dataType = DataType.LESSONS

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LessonVH(parent)

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
        private val deleteBtn by lazy { itemView.delete_btn }
        private val signBtn by lazy { itemView.sign_btn }

        init {
            val visibility = if (isEditable) View.VISIBLE else View.GONE
            editBtn.visibility = visibility
            deleteBtn.visibility = visibility

            signBtn.visibility = if (!isEditable) View.VISIBLE else View.GONE
        }

        override fun bind(lesson: Lesson) {

            DataSource.getUser(lesson.uid)?.let {

                Glide.with(teacherImgView)
                    .load(it.profileImageUrl)
                    .placeholder(R.drawable.yoga_model)
                    .fallback(R.drawable.yoga_model)
                    .circleCrop()
                    .into(teacherImgView)

                teacherNameTv.text = it.name
            }

            if (!isEditable) {
                (DataSource.currentUser as? Teacher)?.let {
                    signBtn.isEnabled = !it.teachingLessonsIDs.contains(lesson.id)

                } ?: signBtn.setEnabled(true)
            }

            lesson.apply {
                kindTv.text = title
                placeTv.text = locationName
                timeTv.text = startDate.relativeTimeString(itemView.context)
            }

            setOnClickListeners(lesson)
        }

        override fun setOnClickListeners(lesson: Lesson) {

            val id = lesson.id
            val i = originalPositions.getOrDefault(id, adapterPosition)

            itemView.setOnClickListener {
                callback?.onItemSelected(lesson, i)
            }

            dropDownBtn.setOnClickListener {

                val isVisible = toggles[id]!!
                it.toggleRotation(isVisible)
                dropDownSection.toggleSlide(isVisible)

                toggles[id] = !isVisible
            }

            if (isEditable) {
                editBtn.setOnClickListener { callback?.onEditAction(lesson, i) }
                deleteBtn.setOnClickListener { callback?.onDeleteAction(lesson, i) }
            } else {
                val isSigned = DataSource.isUserSignedToLesson(lesson)
                signBtn.text = "Sign ${if (isSigned) "out" else "in"}"
                signBtn.setOnClickListener {
                    callback?.onSignAction(lesson, i)
                }
            }

        }
    }
}