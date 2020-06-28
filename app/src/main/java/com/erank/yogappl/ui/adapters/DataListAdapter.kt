package com.erank.yogappl.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.utils.interfaces.OnItemActionCallback
import com.google.android.gms.ads.formats.UnifiedNativeAd

abstract class DataListAdapter<T : BaseData>(
    protected val isEditable: Boolean
) : RecyclerView.Adapter<DataVH<*>>() {

    internal var list: List<Any> = emptyList()

    var callback: OnItemActionCallback<T>? = null

    protected var toggles = mutableMapOf<String, Boolean>()

    abstract val dataType: DataType

    override fun onBindViewHolder(holder: DataVH<*>, pos: Int) {
        holder.bind(list[pos])
    }

    fun submitList(list: List<T>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun submitAd(ad: UnifiedNativeAd) {
        val combinedList = mutableListOf<Any>()
        for ((i, item) in list.withIndex()) {
            combinedList.add(item)
            if (i % NUM_ITEMS_BEFORE_AD == 0) {
                combinedList.add(ad)
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(pos: Int): Int {
        return when {
            pos % NUM_ITEMS_BEFORE_AD == 0 -> DATA_TYPE
            else -> AD_TYPE
        }
    }

    protected val DATA_TYPE = 0
    protected val AD_TYPE = 1
    private val NUM_ITEMS_BEFORE_AD = 3
}