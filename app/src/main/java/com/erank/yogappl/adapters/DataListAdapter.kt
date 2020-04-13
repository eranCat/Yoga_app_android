package com.erank.yogappl.adapters

import android.util.Log
import androidx.recyclerview.widget.ListAdapter
import com.erank.yogappl.adapters.diffs.DataDiffCallback
import com.erank.yogappl.models.BaseData
import com.erank.yogappl.models.DataInfo
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.interfaces.OnItemActionCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class DataListAdapter<T : BaseData, VH : DataVH<T>>(
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

    fun onAddedReceived(info: DataInfo) {
        if (info.type == dataType) {
            Log.d(TAG, "Added to ${info.type}")
        }
    }

    fun onUpdateReceived(info: DataInfo) {
        if (info.type == dataType) {
            Log.d(TAG, "Updated in ${info.type}")
        }
    }

    fun removeAt(position: Int) {
        Log.d(TAG, "removed at [$position]")
    }

    private var originalList: List<T>? = null

    fun filter(query: String) = CoroutineScope(Default).launch {

        if (originalList == null) {
            originalList = ArrayList(currentList)
        }

        val filtered = originalList!!.filter {
            it.title.contains(query, true)
        }

        withContext(Main) { submitList(filtered) }
    }


    fun reset() {
        submitList(originalList)
        originalList = null
    }
}