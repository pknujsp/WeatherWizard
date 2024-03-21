package io.github.pknujsp.everyweather.core.ads

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAdOptions
import io.github.pknujsp.everyweather.core.ads.databinding.NativeAdLayoutBinding
import io.github.pknujsp.everyweather.core.ads.nativetemplates.NativeTemplateStyle

object AdMob {
    const val NATIVE_AD_ID = BuildConfig.ADMOB_NATIVE_AD_ID
    const val BANNER_AD_ID = BuildConfig.ADMOB_BANNER_AD_ID

    private const val NATIVE_AD_TEST_ID = "ca-app-pub-3940256099942544/2247696110"
    private const val BANNER_AD_TEST_ID = "ca-app-pub-3940256099942544/6300978111"

    fun initialize(context: Context) {
        MobileAds.initialize(context) {}
    }

    @Composable
    fun BannerAd(modifier: Modifier = Modifier) {
        AndroidView(modifier = modifier, factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = Ad.BANNER.id
            }
        }) {
            loadAd(it)
        }
    }

    @SuppressLint("MissingPermission")
    @Composable
    fun NativeAd(modifier: Modifier = Modifier) {
        AndroidView(modifier = modifier, factory = { context ->
            NativeAdLayoutBinding.inflate(LayoutInflater.from(context)).apply {
                val adLoader =
                    AdLoader.Builder(context, Ad.NATIVE.id).forNativeAd { nativeAd ->
                        adView.apply {
                            val styles: NativeTemplateStyle = NativeTemplateStyle.Builder().build()
                            setStyles(styles)
                            setNativeAd(nativeAd)
                        }
                    }.withAdListener(
                        object : AdListener() {
                            override fun onAdFailedToLoad(p0: LoadAdError) {
                                root.isVisible = false
                            }

                            override fun onAdLoaded() {
                                root.isVisible = true
                            }
                        },
                    ).withNativeAdOptions(NativeAdOptions.Builder().setRequestCustomMuteThisAd(true).build()).build()
                adLoader.loadAd(AdRequest.Builder().build())
            }.root
        }) {}
    }

    @SuppressLint("MissingPermission")
    @MainThread
    fun loadAd(adView: AdView) {
        adView.loadAd(AdRequest.Builder().build())
    }

    enum class Ad(val id: String) {
        BANNER(if (BuildConfig.DEBUG) BANNER_AD_TEST_ID else BANNER_AD_ID),
        NATIVE(if (BuildConfig.DEBUG) NATIVE_AD_TEST_ID else NATIVE_AD_ID),
    }
}
