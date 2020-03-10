package com.erank.yogappl.adapters.diffs

import androidx.recyclerview.widget.DiffUtil
import com.erank.yogappl.models.BaseData

class DataDiffCallback<T : BaseData> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}