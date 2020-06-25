package com.erank.yogappl.ui.adapters.ads

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.erank.yogappl.ui.adapters.diffs.AdDiffCallback
import com.google.android.gms.ads.formats.UnifiedNativeAd

class AdsAdapter(ads: List<UnifiedNativeAd> = emptyList()) :
    ListAdapter<UnifiedNativeAd,NativeAdVH>(AdDiffCallback.get()) {
    //    TODO convert list to liveData
    var ads: List<UnifiedNativeAd> = ads

    fun updateAds(ads: List<UnifiedNativeAd>) {
        this.ads = ads
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NativeAdVH(parent)

    override fun getItemCount() = ads.size

    override fun onBindViewHolder(holder: NativeAdVH, position: Int) = holder.bind(ads[position])
}