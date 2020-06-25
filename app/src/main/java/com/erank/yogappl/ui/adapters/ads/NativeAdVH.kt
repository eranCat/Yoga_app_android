package com.erank.yogappl.ui.adapters.ads

import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.erank.yogappl.R
import com.erank.yogappl.ui.adapters.DataVH
import com.erank.yogappl.utils.extensions.hide
import com.erank.yogappl.utils.extensions.show
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import kotlinx.android.synthetic.main.ad_unified.view.*

class NativeAdVH(parent: ViewGroup) :
    DataVH<UnifiedNativeAd>(parent, R.layout.ad_unified) {

    private val root by lazy { itemView as UnifiedNativeAdView }

    private val headlineView by lazy { itemView.ad_headline }
    private val bodyView by lazy { itemView.ad_body }
    private val iconView by lazy { itemView.ad_icon }
    private val mediaView by lazy { itemView.ad_media }

    init {
        root.headlineView = headlineView
        root.bodyView = bodyView
        root.callToActionView = itemView
        root.iconView = iconView
    }

    override fun bind(ad: UnifiedNativeAd) = with(ad) {
        headlineView.text = headline
        bodyView.text = body

        icon?.let {
            iconView.show()
            Glide.with(iconView)
                .load(it.drawable)
                .transform(RoundedCorners(16))
                .into(iconView)

        } ?: iconView.hide()

        mediaContent?.let {
            mediaView.show()
            mediaView.setMediaContent(it)
        } ?: mediaView.hide()

        root.setNativeAd(this)
    }
}