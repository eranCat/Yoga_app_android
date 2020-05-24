package com.erank.yogappl.ui.fragments

import androidx.core.os.bundleOf
import com.erank.yogappl.ui.adapters.LessonsAdapter
import com.erank.yogappl.data.models.Lesson
import com.erank.yogappl.data.data_source.DataSource
import com.erank.yogappl.data.enums.DataType.LESSONS
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.utils.interfaces.TaskCallback


class LessonsListFragment : DataListFragment<Lesson, LessonsAdapter, LessonsAdapter.LessonVH>() {

    companion object {
        val TAG = LessonsListFragment::class.java.name

        fun newInstance(type: SourceType) =
            LessonsListFragment().apply {
                arguments = bundleOf(SOURCE_TYPE to type)
            }
    }

    override val dataType = LESSONS

    override fun createAdapter() =
        initAdapter(LessonsAdapter(isEditable))


    override fun onDeleteConfirmed(item: Lesson) =
        DataSource.deleteLesson(item, this)


    override fun toggleSign(item: Lesson, callback: TaskCallback<Boolean, Exception>) {
        DataSource.toggleSignToLesson(item, callback)
    }

    override fun getLiveData() =
        DataSource.getLessons(currentSourceType)

}