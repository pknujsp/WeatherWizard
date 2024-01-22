package io.github.pknujsp.weatherwizard.core.ads

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.MainThread
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds


object AdMob {

    const val NATIVE_AD_ID = BuildConfig.ADMOB_NATIVE_AD_ID
    const val BANNER_AD_ID = BuildConfig.ADMOB_BANNER_AD_ID

    fun initialize(context: Context) {
        MobileAds.initialize(context) {}
    }

    fun createAdView(context: Context, ad: Ad): AdView {
        return AdView(context).apply {
            setAdSize(ad.adSize)
            adUnitId = ad.adUnitId
        }
    }


    @SuppressLint("MissingPermission")
    @MainThread
    fun loadAd(adView: AdView) {
        adView.loadAd(AdRequest.Builder().build())
    }

    enum class Ad(val adSize: AdSize, val adUnitId: String) {
        NATIVE(AdSize.FLUID, NATIVE_AD_ID), BANNER(AdSize.BANNER, BANNER_AD_ID)
    }
}