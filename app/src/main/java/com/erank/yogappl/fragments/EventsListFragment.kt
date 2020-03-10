package com.erank.yogappl.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.erank.yogappl.R
import com.erank.yogappl.adapters.EventsAdapter
import com.erank.yogappl.models.Event
import com.erank.yogappl.utils.data_source.DataSource
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.enums.SourceType
import com.erank.yogappl.utils.interfaces.TaskCallback

class EventsListFragment : DataListFragment<Event, EventsAdapter, EventsAdapter.EventVH>() {
    companion object {
        fun newInstance(sourceType: SourceType) =
            EventsListFragment().apply {
                arguments = argsBundle(sourceType)
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

    override fun createAdapter(list: MutableList<Event>) =
        initAdapter(EventsAdapter(list, isEditable))

    override fun getLiveData(sourceType: SourceType) =
        DataSource.getEvents(sourceType)

    override fun onDeleteConfirmed(item: Event, pos: Int) =
        DataSource.deleteEvent(item, pos, this)

    override fun toggleSign(item: Event, callback: TaskCallback<Boolean, Exception>) =
        DataSource.toggleSignToEvent(item, callback)

}