package com.erank.yogappl.utils.helpers

import android.content.Context
import com.erank.yogappl.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.formats.UnifiedNativeAd

object AdsManager {
    private const val IS_AD_TESTING = true
    private const val NUMBER_OF_ADS = 5

    private const val LIST_AD_ID = "ca-app-pub-2280152533389019/3632312698"
    private const val TEST_LIST_AD_ID = "ca-app-pub-3940256099942544/2247696110"

    fun loadBannerAd(adView: AdView) {
        val adRequest = AdRequest.Builder().build()

        val id = if (!IS_AD_TESTING) R.string.banner_ad_id
        else R.string.test_ad_banner_id

        val unitId = adView.context.getString(id)
        adView.adUnitId = unitId

        adView.loadAd(adRequest)
    }

    private var nativeAds = mutableListOf<UnifiedNativeAd>()
    private var adLoader: AdLoader? = null

    fun loadNativeAds(
        context: Context,
        callback: (ads: List<UnifiedNativeAd>) -> Unit
    ) {
        val adUnitId = if (!IS_AD_TESTING) LIST_AD_ID
        else TEST_LIST_AD_ID

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
            .build()

        val adRequest = AdRequest.Builder().build()
        adLoader?.loadAds(adRequest, NUMBER_OF_ADS)
    }
}