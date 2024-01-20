package io.github.pknujsp.weatherwizard.feature.weather.summary

import androidx.compose.runtime.Stable

@Stable
interface SummaryUiState {
    val isSummarizing: Boolean
    val summaryText: String?
    val error: String?
}