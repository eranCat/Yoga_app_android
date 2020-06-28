package com.erank.yogappl.utils.helpers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.erank.yogappl.R
import com.erank.yogappl.utils.extensions.toast
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.UnifiedNativeAd
import javax.inject.Singleton

@Singleton
class AdsManager( context: Context) {

    companion object {
        private const val IS_AD_TESTING = true
        private const val NUMBER_OF_ADS = 3
    }

    private var adLoader: AdLoader
    private var nativeAd = MutableLiveData<UnifiedNativeAd?>()

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
        adLoader = AdLoader.Builder(context, adUnitId)
            .forUnifiedNativeAd {
                nativeAd.value = it
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    // Handle the failure by logging, altering the UI, and so on.
                    context.toast("Failed loading ad")
                }
            })
            .build()
    }

    fun loadBannerAd(context: Context): AdView = AdView(context).apply {
        adSize = AdSize.BANNER
        adUnitId = bannerAdId
        val adRequest = AdRequest.Builder().build()
        loadAd(adRequest)
    }

    fun loadNativeAds(): LiveData<UnifiedNativeAd?> {
        val adRequest = AdRequest.Builder().build()
        adLoader.loadAds(adRequest, NUMBER_OF_ADS)
        return nativeAd
    }
}