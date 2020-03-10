package com.erank.yogappl.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erank.yogappl.R
import com.erank.yogappl.adapters.diffs.LocationDiffCallback
import com.erank.yogappl.models.LocationResult
import com.erank.yogappl.utils.interfaces.OnLocationSelectedCallback
import kotlinx.android.synthetic.main.location_item.view.*


class LocationsAdapter :
    ListAdapter<LocationResult, LocationsAdapter.LocationHolder> {

    private val callback: OnLocationSelectedCallback

    constructor(callback: OnLocationSelectedCallback) : super(
        AsyncDifferConfig.Builder(
            LocationDiffCallback()
        ).build()
    ) {
        this.callback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LocationHolder(parent)

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        val location = getItem(position)
        holder.fill(location)
        holder.setOnClickListener(location, callback)
    }

    class LocationHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.location_item, parent, false)
    ) {
        private val localNameTV = itemView.location_item_name_tv
        private val locStreetTv = itemView.location_item_street
        private val locationDistanceTV = itemView.location_item_distance_tv

        fun fill(location: LocationResult) {

            localNameTV.text = location.POI?.name ?: location.address.localName
            with(location.address) {
                var name = streetName?.let { streetName } ?: freeformAddress
                if (streetNumber > 0) {
                    name += " $streetNumber"
                }
                locStreetTv.text = name
            }

            locationDistanceTV.text = location.distance.formattedDistance()


        }

        fun setOnClickListener(
            location: LocationResult,
            callback: OnLocationSelectedCallback
        ) = itemView.setOnClickListener {
            callback.onSelected(location)
        }
    }

}

private fun Double.formattedDistance(): String {
    if (this < 1000) {
        return String.format("%.0f meters", this)
    }

    return String.format("%.1f km", this / 1000)
}
