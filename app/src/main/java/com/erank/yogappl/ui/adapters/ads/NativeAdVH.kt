package com.erank.yogappl.ui.adapters.ads

import android.view.ViewGroup
import com.erank.yogappl.R
import com.erank.yogappl.ui.adapters.DataVH
import com.erank.yogappl.utils.extensions.hide
import com.erank.yogappl.utils.extensions.show
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import kotlinx.android.synthetic.main.ad_unified.view.*

class NativeAdVH(parent: ViewGroup) :
    DataVH<UnifiedNativeAd>(parent,
        R.layout.ad_unified
    ) {

    private val root by lazy { itemView as UnifiedNativeAdView }

    private val headlineView by lazy { itemView.ad_headline }
    private val bodyView by lazy { itemView.ad_body }
    private val callToActionView by lazy { itemView.ad_call_to_action }
    private val iconView by lazy { itemView.ad_icon }
    private val priceView by lazy { itemView.ad_price }
    private val storeView by lazy { itemView.ad_store }
    private val advertiserView by lazy { itemView.ad_advertiser }
    private val starRatingView by lazy { itemView.ad_rating }

    init {
        root.headlineView = headlineView
        root.bodyView = bodyView
        root.callToActionView = callToActionView
        root.iconView = iconView
        root.priceView = priceView
        root.starRatingView = starRatingView
        root.storeView = storeView
        root.advertiserView = advertiserView
    }

    override fun bind(ad: UnifiedNativeAd) = with(ad) {

        headlineView.text = headline
        bodyView.text = body
        callToActionView.text = callToAction

        icon?.let {
            iconView.show()
            iconView.setImageDrawable(it.drawable)
        } ?: iconView.hide()

        price?.let {
            priceView.text = it
            priceView.show()
        } ?: priceView.hide()

        starRating?.let {
            starRatingView.rating = it.toFloat()
            starRatingView.show()
        } ?: starRatingView.hide()

        store?.let {
            storeView.text = it
            storeView.show()
        } ?: storeView.hide()

        advertiser?.let {
            advertiserView.text = it
            advertiserView.show()
        } ?: advertiserView.hide()

        root.setNativeAd(ad)
    }

    override fun setOnClickListeners(ad: UnifiedNativeAd) {

    }

}