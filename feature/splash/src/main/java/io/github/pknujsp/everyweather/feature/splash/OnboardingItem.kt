package io.github.pknujsp.everyweather.feature.splash

import androidx.compose.runtime.Stable
import io.github.pknujsp.everyweather.core.common.FeatureType

@Stable
internal interface OnboardingItem {
    val title: Int
    val message: Int
    val image: Int
}

internal class DefaultOnboardingItem(
    override val title: Int,
    override val message: Int,
    override val image: Int,
) : OnboardingItem

internal class PemissionOnboardingItem(
    override val title: Int,
    override val message: Int,
    override val image: Int,
    val permissions: List<FeatureType.Permission>,
) : OnboardingItem

internal val onboardingItems: List<OnboardingItem> =
    listOf(
        DefaultOnboardingItem(
            title = io.github.pknujsp.everyweather.core.resource.R.string.onboarding_title_main,
            message = io.github.pknujsp.everyweather.core.resource.R.string.onboarding_message_main,
            image = io.github.pknujsp.everyweather.core.resource.R.drawable.weatherwizard_icon_logo,
        ),
        DefaultOnboardingItem(
            title = io.github.pknujsp.everyweather.core.resource.R.string.onboarding_title_notification,
            message = io.github.pknujsp.everyweather.core.resource.R.string.onboarding_message_notification,
            image = io.github.pknujsp.everyweather.core.resource.R.drawable.notifications,
        ),
        DefaultOnboardingItem(
            title = io.github.pknujsp.everyweather.core.resource.R.string.onboarding_title_widget,
            message = io.github.pknujsp.everyweather.core.resource.R.string.onboarding_message_widget,
            image = io.github.pknujsp.everyweather.core.resource.R.drawable.widgets,
        ),
    )