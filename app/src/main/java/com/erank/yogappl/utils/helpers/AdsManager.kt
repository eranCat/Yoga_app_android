package com.erank.yogappl.utils.helpers

import android.content.Context
import android.util.Log
import com.erank.yogappl.R
import com.erank.yogappl.utils.extensions.toast
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.UnifiedNativeAd
import javax.inject.Singleton

@Singleton
class AdsManager(val context: Context) {

    companion object {
        private const val IS_AD_TESTING = true
        private const val NUMBER_OF_ADS = 3
    }

    private val bannerAdId: String
    private val adUnitId: String

    init {

        bannerAdId = context.getString(
            if (IS_AD_TESTING) R.string.test_ad_banner_id
            else R.string.list_ad_id
        )
        adUnitId = context.getString(
            if (IS_AD_TESTING) R.string.test_list_ad_id
            else R.string.list_ad_id
        )

    }

    fun loadBannerAd(): AdView = AdView(context).apply {
        adSize = AdSize.BANNER
        adUnitId = bannerAdId
        loadAd(adRequest())
    }

    fun loadNativeAds(callback: (UnifiedNativeAd) -> Unit) {
        AdLoader.Builder(context, adUnitId)
            .forUnifiedNativeAd {
                callback(it)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    // Handle the failure by logging, altering the UI, and so on.
                    Log.d("Ad", "Failed loading ad - $errorCode")
                    context.toast("Failed loading ad")
                }
            })
            .build()
            .loadAds(adRequest(), NUMBER_OF_ADS)
    }

    private fun adRequest() = AdRequest.Builder().build()
}