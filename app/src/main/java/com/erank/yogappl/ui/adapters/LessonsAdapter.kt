package com.erank.yogappl.ui.adapters

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.erank.yogappl.R
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.models.Lesson
import com.erank.yogappl.data.models.PreviewUser
import com.erank.yogappl.utils.extensions.relativeTimeString
import com.erank.yogappl.utils.extensions.toggleRotation
import com.erank.yogappl.utils.extensions.toggleSlide
import kotlinx.android.synthetic.main.drop_down_btn.view.*
import kotlinx.android.synthetic.main.dropdown_menu.view.*
import kotlinx.android.synthetic.main.lesson_item.view.*
import kotlinx.android.synthetic.main.profile_image.view.*


class LessonsAdapter(
    isEditable: Boolean,
    private val userUploads: List<String>,
    private val signed: MutableList<String>
) :
    DataListAdapter<Lesson, LessonsAdapter.LessonVH>(isEditable) {

    private var users: Map<String, PreviewUser> = emptyMap()
    override val dataType = DataType.LESSONS

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LessonVH(parent)

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
        private val deleteBtn by lazy { itemView.delete_btn }
        private val signBtn by lazy { itemView.sign_btn }

        init {
            val visibility = if (isEditable) View.VISIBLE else View.GONE
            editBtn.visibility = visibility
            deleteBtn.visibility = visibility

            signBtn.visibility = if (isEditable) View.GONE else View.VISIBLE
        }

        override fun bind(item: Lesson) {

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
                deleteBtn.setOnClickListener {
                    callback?.onDeleteAction(item)
                }
                return
            }

            val state =
                if (signed.contains(item.id)) "out"
                else "in"

            signBtn.text = itemView.resources.getString(R.string.signMsg, state)

            signBtn.setOnClickListener {
                callback?.onSignAction(item)
            }

        }
    }

}
