package com.erank.yogappl.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.erank.yogappl.R
import com.erank.yogappl.adapters.EventsAdapter
import com.erank.yogappl.models.Event
import com.erank.yogappl.utils.data_source.DataSource
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.enums.SourceType
import com.erank.yogappl.utils.interfaces.TaskCallback

class EventsListFragment : DataListFragment<Event, EventsAdapter, EventsAdapter.EventVH>() {
    companion object {
        fun newInstance(type: SourceType) =
            EventsListFragment().apply {
                arguments = bundleOf(SOURCE_TYPE to type)
            }
    }

    override val dataType = DataType.EVENTS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(
        R.layout.fragment_data_list,
        container, false
    )

    override fun createAdapter() =
        initAdapter(EventsAdapter(isEditable))


    override fun getLiveData() = DataSource.getEvents(currentSourceType)

    override fun onDeleteConfirmed(item: Event) =
        DataSource.deleteEvent(item, this)

    override fun toggleSign(item: Event, callback: TaskCallback<Boolean, Exception>) =
        DataSource.toggleSignToEvent(item, callback)


}