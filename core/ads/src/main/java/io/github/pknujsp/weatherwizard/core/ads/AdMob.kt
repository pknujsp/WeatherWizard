package io.github.pknujsp.weatherwizard.core.ads

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
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

    private fun createAdView(context: Context, ad: Ad): AdView {
        return AdView(context).apply {
            setAdSize(ad.adSize)
            adUnitId = ad.adUnitId
        }
    }

    @Composable
    fun AdView(modifier: Modifier = Modifier, ad: Ad) {
        val context = LocalContext.current

        AndroidView(modifier = modifier, factory = { createAdView(context, ad) }) {
            loadAd(it)
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