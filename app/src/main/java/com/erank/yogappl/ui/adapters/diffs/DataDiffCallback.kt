package com.erank.yogappl.ui.adapters.diffs

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.data.models.PreviewData

class DataDiffCallback<T : BaseData> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem

    companion object {
        fun <T : BaseData> get() =
            AsyncDifferConfig.Builder(DataDiffCallback<T>()).build()
    }
}