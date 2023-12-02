package io.github.pknujsp.weatherwizard.feature.notification.daily.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

data class DailyNotificationState(
    val dailyNotificationUiState: DailyNotificationUiState
)


@Composable
fun rememberDailyNotificationState(dailyNotificationUiState: DailyNotificationUiState) = remember(dailyNotificationUiState) {
    DailyNotificationState(dailyNotificationUiState)
}