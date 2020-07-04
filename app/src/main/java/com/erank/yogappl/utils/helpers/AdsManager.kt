package com.erank.yogappl.utils.helpers

import android.content.Context
import com.erank.yogappl.R
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import javax.inject.Singleton

@Singleton
class AdsManager(val context: Context) {

    companion object {
        private const val IS_TESTING = false
    }

    private val bannerAdId: String
    private val adUnitId: String

    init {

        val bannerRes = if (!IS_TESTING) R.string.banner_ad_id
        else R.string.test_ad_banner_id
        bannerAdId = context.getString(bannerRes)

        val adUnitRes = if (!IS_TESTING) R.string.list_ad_id
        else R.string.test_list_ad_id
        adUnitId = context.getString(adUnitRes)

    }

    fun loadBannerAd(): AdView = AdView(context).apply {
        adSize = AdSize.BANNER
        adUnitId = bannerAdId
        loadAd(adRequest())
    }

    fun loadNativeAds(callback: (UnifiedNativeAd) -> Unit) {
        AdLoader.Builder(context, adUnitId)
            .forUnifiedNativeAd { callback(it) }
            .build()
            .loadAds(adRequest(), 1)
    }

    private fun adRequest() = AdRequest.Builder().build()
}