package com.erank.yogappl.ui.adapters.diffs

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import com.google.android.gms.ads.formats.UnifiedNativeAd

class AdDiffCallback : DiffUtil.ItemCallback<UnifiedNativeAd>() {

    override fun areItemsTheSame(oldItem: UnifiedNativeAd, newItem: UnifiedNativeAd) =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: UnifiedNativeAd, newItem: UnifiedNativeAd) =
        oldItem.body == newItem.body// TODO add equals

    companion object {
        fun get() = AsyncDifferConfig.Builder(AdDiffCallback()).build()
    }
}