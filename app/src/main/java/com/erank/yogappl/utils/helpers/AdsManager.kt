package com.erank.yogappl.utils.helpers

import android.content.Context
import com.erank.yogappl.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.UnifiedNativeAd

object AdsManager {
    private const val IS_AD_TESTING = true
    private const val NUMBER_OF_ADS = 1

    fun loadBannerAd(context: Context): AdView {
        val unitId = context.getString(
            if (IS_AD_TESTING) R.string.test_ad_banner_id
            else R.string.list_ad_id
        )

        return AdView(context).apply {
            adSize = AdSize.BANNER
            adUnitId = unitId
            val adRequest = AdRequest.Builder().build()
            loadAd(adRequest)
        }
    }

    private var nativeAds = mutableListOf<UnifiedNativeAd>()
    private var adLoader: AdLoader? = null

    fun loadNativeAds(
        context: Context,
        callback: (ads: List<UnifiedNativeAd>) -> Unit
    ) {
        val adUnitId = context.getString(
            if (IS_AD_TESTING) R.string.test_list_ad_id
            else R.string.list_ad_id
        )

        adLoader = AdLoader.Builder(context, adUnitId)
            .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                // Show the ad.
                nativeAds.add(ad)
                if (adLoader?.isLoading != true) {
                    callback(nativeAds)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    // Handle the failure by logging, altering the UI, and so on.
                }
            })
            .build().apply {
                val adRequest = AdRequest.Builder().build()
                loadAds(adRequest, NUMBER_OF_ADS)
            }

    }
}