package com.erank.yogappl.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.ui.adapters.ads.NativeAdVH
import com.erank.yogappl.utils.interfaces.OnItemActionCallback
import com.google.android.gms.ads.formats.UnifiedNativeAd

abstract class DataListAdapter<T : BaseData>(
    protected val isEditable: Boolean
) : RecyclerView.Adapter<DataVH<*>>() {

    private var hasAd: Boolean = false
    private var dataCount: Int = 0
    internal var list: MutableList<Any> = mutableListOf()

    var callback: OnItemActionCallback<T>? = null

    protected var toggles = mutableMapOf<String, Boolean>()

    abstract val dataType: DataType

    override fun onBindViewHolder(holder: DataVH<*>, pos: Int) = holder.bind(list[pos])

    fun submitList(list: List<T>) {
        this.list = list.toMutableList()
        this.dataCount = list.size
        hasAd = false
        notifyDataSetChanged()
    }

    fun submitAd(ad: UnifiedNativeAd) {
        hasAd = true
        val first = NUM_ITEMS_BEFORE_AD
        for (i in first..dataCount step first) {
            list.add(i, ad)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(pos: Int): Int = when {
        !hasAd -> DATA_TYPE
        pos < NUM_ITEMS_BEFORE_AD -> DATA_TYPE
        pos % NUM_ITEMS_BEFORE_AD == 0 -> AD_TYPE
        else -> DATA_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        AD_TYPE -> NativeAdVH(parent)
        else -> createDataViewHolder(parent)
    }

    abstract fun createDataViewHolder(parent: ViewGroup): DataVH<*>

    private val DATA_TYPE = 0
    private val AD_TYPE = 1
    private val NUM_ITEMS_BEFORE_AD = 2
}