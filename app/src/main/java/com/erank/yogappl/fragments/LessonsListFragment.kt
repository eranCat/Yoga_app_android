package com.erank.yogappl.fragments

import androidx.core.os.bundleOf
import com.erank.yogappl.adapters.LessonsAdapter
import com.erank.yogappl.models.Lesson
import com.erank.yogappl.utils.data_source.DataSource
import com.erank.yogappl.utils.enums.DataType.LESSONS
import com.erank.yogappl.utils.enums.SourceType
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