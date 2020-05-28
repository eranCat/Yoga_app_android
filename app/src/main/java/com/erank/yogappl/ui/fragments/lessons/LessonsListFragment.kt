package com.erank.yogappl.ui.fragments.lessons

import android.content.Context
import androidx.core.os.bundleOf
import com.erank.yogappl.data.enums.DataType.LESSONS
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.models.Lesson
import com.erank.yogappl.data.models.Teacher
import com.erank.yogappl.ui.adapters.LessonsAdapter
import com.erank.yogappl.ui.fragments.DataListFragment
import com.erank.yogappl.utils.App
import com.erank.yogappl.utils.runOnBackground
import kotlinx.android.synthetic.main.fragment_data_list.*
import javax.inject.Inject


class LessonsListFragment : DataListFragment<Lesson, LessonsAdapter, LessonsAdapter.LessonVH>() {

    companion object {
        val TAG = LessonsListFragment::class.java.name

        fun newInstance(type: SourceType) =
            LessonsListFragment().apply {
                arguments = bundleOf(SOURCE_TYPE to type)
            }
    }

    override val dataType = LESSONS

    @Inject
    lateinit var viewModel: LessonsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).getAppComponent().inject(this)
    }

    override fun createAdapter(): LessonsAdapter {
        val uploads = (viewModel.user as? Teacher)?.teachingLessonsIDs
            ?: emptySet<String>()
        val signed = viewModel.user!!.signedEventsIDS
        val adapter = LessonsAdapter(isEditable, uploads, signed)
        return initAdapter(adapter)
    }

    //    TODO add index for delete
    override fun onDeleteConfirmed(item: Lesson) {
        runOnBackground({ viewModel.deleteLesson(item) }) {
            data_recycler_view.adapter?.notifyDataSetChanged()
        }
    }

    override suspend fun toggleSign(item: Lesson) =
        viewModel.toggleSignToLesson(item)

    override fun getLiveData() = viewModel.getLessons(currentSourceType)

    override fun onListUpdated(list: List<Lesson>) = getUsers(list)

    override suspend fun getFilteredData(query: String) =
        viewModel.getFilteredLessons(currentSourceType, query)

    private fun getUsers(list: List<Lesson>) {
        runOnBackground({ viewModel.getUsersMap(list) }) { it ->
            with(dataAdapter) {
                setUsers(it)
                notifyDataSetChanged()
            }
        }
    }
}