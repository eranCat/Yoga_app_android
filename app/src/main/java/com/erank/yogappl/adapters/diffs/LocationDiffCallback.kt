package com.erank.yogappl.adapters.diffs

import androidx.recyclerview.widget.DiffUtil
import com.erank.yogappl.models.LocationResult

class LocationDiffCallback : DiffUtil.ItemCallback<LocationResult>() {

    override fun areItemsTheSame(
        oldItem: LocationResult,
        newItem: LocationResult
    ) = oldItem.location == newItem.location

    override fun areContentsTheSame(
        oldItem: LocationResult,
        newItem: LocationResult
    ) = oldItem == newItem

}