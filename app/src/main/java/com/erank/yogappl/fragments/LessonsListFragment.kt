package com.erank.yogappl.fragments

import com.erank.yogappl.adapters.LessonsAdapter
import com.erank.yogappl.models.Lesson
import com.erank.yogappl.utils.data_source.DataSource
import com.erank.yogappl.utils.enums.DataType.LESSONS
import com.erank.yogappl.utils.enums.SourceType
import com.erank.yogappl.utils.interfaces.TaskCallback


class LessonsListFragment : DataListFragment<Lesson, LessonsAdapter, LessonsAdapter.LessonVH>() {

    companion object {
        val TAG: String = LessonsListFragment::class.java.name

        fun newInstance(sourceType: SourceType) = LessonsListFragment()
            .apply {
                arguments = argsBundle(sourceType)
            }
    }

    override val dataType = LESSONS

    override fun createAdapter(lessons: MutableList<Lesson>) =
        initAdapter(LessonsAdapter(lessons, isEditable))

    override fun onDeleteConfirmed(item: Lesson, pos: Int) =
        DataSource.deleteLesson(item, pos, this)


    override fun toggleSign(item: Lesson, callback: TaskCallback<Boolean, Exception>) {
        DataSource.toggleSignToLesson(item, callback)
    }

    override fun getLiveData(sourceType: SourceType) = DataSource.getLessons(sourceType)

}