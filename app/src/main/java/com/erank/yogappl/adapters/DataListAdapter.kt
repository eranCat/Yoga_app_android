package com.erank.yogappl.adapters

import androidx.recyclerview.widget.AsyncDifferConfig
import com.erank.yogappl.adapters.diffs.DataDiffCallback
import com.erank.yogappl.models.BaseData
import com.erank.yogappl.models.DataInfo
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.interfaces.OnItemActionCallback

abstract class DataListAdapter<T : BaseData, VH : DataVH<T>> : SearchableListAdapter<T, VH> {

    protected val isEditable: Boolean

    constructor(list: MutableList<T>, isEditable: Boolean) :
            super(
                list,
                AsyncDifferConfig.Builder<T>(DataDiffCallback()).build()
            ) {
        this.isEditable = isEditable
        initOriginalPositions()
    }

    var callback: OnItemActionCallback<T>? = null

    protected lateinit var toggles: MutableMap<String, Boolean>
    override lateinit var originalPositions: Map<String, Int>

    abstract val dataType: DataType

    fun initOriginalPositions() {

        originalPositions = currentList
            .mapIndexed { i, data -> data.id to i }
            .associate { it }

        toggles = currentList
            .associate { it.id to false }
            .toMutableMap()
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(currentList[position])

    override fun onAddedReceived(info: DataInfo) {
        if (info.dataType == dataType) {
            notifyItemInserted(info.position!!)
        }
    }

    override fun onUpdateReceived(info: DataInfo) {
        if (info.dataType == dataType) {
            notifyItemChanged(info.position!!)
        }
    }

    fun removeAt(position: Int) {
        notifyItemRemoved(position)
    }

    fun addItem(item: T) {
        notifyItemInserted(itemCount - 1)
    }
}