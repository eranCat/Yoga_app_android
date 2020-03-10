package com.erank.yogappl.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.erank.yogappl.activities.MainActivity
import com.erank.yogappl.adapters.SearchableListAdapter
import com.erank.yogappl.models.DataInfo
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.enums.SearchState
import com.erank.yogappl.utils.interfaces.Searchable

class SearchBroadcastReceiver<T : Searchable, VH : RecyclerView.ViewHolder>
    (val onSearchCallback: (isSearching: Boolean, isEmpty: Boolean) -> Unit) : BroadcastReceiver() {

    private var adapter: SearchableListAdapter<T, VH>? = null
    fun setAdapter(adapter: SearchableListAdapter<T, VH>) {
        this.adapter = adapter
    }

    override fun onReceive(context: Context, intent: Intent) {
        val adapter = adapter ?: return

        when (intent.action) {
            MainActivity.ACTION_ADDED ->
                extractData(intent, adapter::onAddedReceived)

            MainActivity.ACTION_UPDATE ->
                extractData(intent, adapter::onUpdateReceived)

            MainActivity.SEARCH_ACTION -> intent.extras?.run {
                val state = intent.getSerializableExtra("state") as SearchState
                val query = intent.getStringExtra("query") ?: ""
                onSearchReceived(adapter, state, query)
            }
        }
    }

    private fun extractData(intent: Intent, callback: (DataInfo) -> Unit) =
        callback(intent.getParcelableExtra("dataInfo"))

    private fun onSearchReceived(
        adapter: SearchableListAdapter<T, VH>,
        state: SearchState, query: String
    ) = when (state) {
        SearchState.CHANGED -> {
            adapter.onSearch(query)
            updateList(adapter)
        }
        SearchState.OPENED -> {
            adapter.onStartSearch()
            updateList(adapter)
        }
        SearchState.CLOSED -> {
            adapter.onEndSearch()
            onSearchCallback(false, false)
        }
    }

    private fun updateList(adapter: SearchableListAdapter<T, VH>) {
        val isEmpty = adapter.currentList.isEmpty()
        onSearchCallback(true, isEmpty)
    }
}