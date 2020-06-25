package com.erank.yogappl.ui.fragments.events

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.erank.yogappl.R
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.models.Event
import com.erank.yogappl.ui.adapters.EventsAdapter
import com.erank.yogappl.ui.fragments.DataListFragment
import com.erank.yogappl.utils.App
import com.erank.yogappl.utils.runOnBackground
import javax.inject.Inject

class EventsListFragment : DataListFragment<Event, EventsAdapter>() {
    companion object {
        fun newInstance(type: SourceType) =
            EventsListFragment().apply {
                arguments = bundleOf(SOURCE_TYPE to type)
            }
    }

    override val dataType = DataType.EVENTS

    @Inject
    lateinit var viewModel: EventsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(
        R.layout.fragment_data_list,
        container, false
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).getAppComponent().inject(this)
    }

    override fun createAdapter(): EventsAdapter {
        val createdEventsIDs = viewModel.user!!.createdEventsIDs
        val signed = viewModel.user!!.signedEventsIDS
        val adapter = EventsAdapter(isEditable, createdEventsIDs, signed)
        return initAdapter(adapter)
    }


    override fun getLiveData() = viewModel.getEvents(currentSourceType)

    override suspend fun getFilteredData(query: String): List<Event> {
        return viewModel.getFilteredEvents(currentSourceType, query)
    }

    override suspend fun toggleSign(item: Event) =
        viewModel.toggleSignToEvent(item)
}