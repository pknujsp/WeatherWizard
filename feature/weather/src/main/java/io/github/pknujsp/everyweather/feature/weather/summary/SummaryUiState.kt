package io.github.pknujsp.everyweather.feature.weather.summary

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable

@Stable
interface SummaryUiState {
    val isSummarizing: Boolean
    val summaryText: String
    val isWaitingFirstResponse: Boolean
    @get:StringRes val error: Int?
    @get:StringRes val buttonText: Int
    val isStopped: Boolean
}