package io.github.pknujsp.weatherwizard.feature.weather.comparison.dailyforecast

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation

@Composable
fun CompareDailyForecastScreen(args: RequestWeatherDataArgs,popBackStack: () -> Unit) {
    BackHandler {
        popBackStack()
    }
    Column {
        TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string
            .title_comparison_daily_forecast)) {
            popBackStack()
        }

    }
}