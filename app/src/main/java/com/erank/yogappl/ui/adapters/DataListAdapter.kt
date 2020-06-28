package com.erank.yogappl.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.utils.interfaces.OnItemActionCallback
import com.google.android.gms.ads.formats.UnifiedNativeAd

abstract class DataListAdapter<T : BaseData>(
    protected val isEditable: Boolean
) : RecyclerView.Adapter<DataVH<*>>() {


    internal var dataList = emptyList<T>()
    protected var adsList = emptyList<UnifiedNativeAd>()
    var callback: OnItemActionCallback<T>? = null

    protected var toggles = mutableMapOf<String, Boolean>()

    abstract val dataType: DataType

    override fun onBindViewHolder(holder: DataVH<*>, pos: Int) {
        when (getItemViewType(pos)) {
            DATA_TYPE -> if (dataList.isNotEmpty()) {
                val diff = pos / NUM_ITEMS_BEFORE_AD
                holder.bind(dataList[pos-diff])
            }
            AD_TYPE -> if (adsList.isNotEmpty()) {
                val i = pos % adsList.size
                holder.bind(adsList[i])
            }
        }
    }

    fun submitList(list: List<T>) {
        this.dataList = list
        notifyDataSetChanged()
    }

    fun submitAdsList(list: List<UnifiedNativeAd>) {
        this.adsList = list
    }

    override fun getItemCount(): Int {
        val size = dataList.size
        return size + size % NUM_ITEMS_BEFORE_AD
    }

    override fun getItemViewType(pos: Int): Int {
        return when {
            adsList.isEmpty() -> DATA_TYPE
            pos % NUM_ITEMS_BEFORE_AD != 0 -> DATA_TYPE
            else -> AD_TYPE
        }
    }

    protected val DATA_TYPE = 0
    protected val AD_TYPE = 1
    private val NUM_ITEMS_BEFORE_AD = 3
}