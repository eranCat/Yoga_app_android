package com.erank.yogappl.ui.adapters

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.ui.adapters.diffs.DataDiffCallback
import com.erank.yogappl.utils.interfaces.OnItemActionCallback

abstract class DataListAdapter<T : BaseData,VH: DataVH<T>>(
    protected val isEditable: Boolean
) : ListAdapter<T, VH>(DataDiffCallback.get()) {

    companion object {
        private val TAG = DataListAdapter::class.java.name
    }

    var callback: OnItemActionCallback<T>? = null

    protected var toggles = mutableMapOf<String, Boolean>()

    abstract val dataType: DataType

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(currentList[position])
}