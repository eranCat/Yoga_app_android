package com.erank.yogappl.adapters

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erank.yogappl.models.DataInfo
import com.erank.yogappl.utils.interfaces.Searchable

abstract class SearchableListAdapter<T : Searchable, VH : RecyclerView.ViewHolder>
    (list: List<T>, config: AsyncDifferConfig<T>) : ListAdapter<T, VH>(config) {

    private var fullList: List<T>

    abstract var originalPositions: Map<String, Int>//associate with ids

    init {
        this.submitList(list)
        fullList = list
    }

    abstract fun onUpdateReceived(info: DataInfo)

    abstract fun onAddedReceived(info: DataInfo)

    fun onStartSearch() {
        fullList = ArrayList(currentList)//copy
    }

    fun onSearch(query: String) {
        val filtered = fullList.filter { it.searchApplies(query) }
        submitList(filtered)
        notifyDataSetChanged()
    }

    fun onEndSearch() {
        submitList(fullList)
        notifyDataSetChanged()
    }
}