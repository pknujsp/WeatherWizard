package io.github.pknujsp.weatherwizard.feature.weather.comparison.hourlyforecast

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation


@Composable
fun CompareHourlyForecastScreen(args: RequestWeatherDataArgs, popBackStack: () -> Unit) {
    // savedStatehandle with  args
    val viewModel: CompareHourlyForecastViewModel = hiltViewModel()
    BackHandler {
        popBackStack()
    }


    Column {
        TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.title_comparison_hourly_forecast)) {
            popBackStack()
        }
        val compareForecastCard = remember {
            CompareForecastCard()
        }
        compareForecastCard.CompareCardSurface {
            LaunchedEffect(Unit){
                viewModel.load(args)
            }


        }
    }
}

@Stable
class CompareForecastCard {

    private val surfaceModifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 12.dp)
        .navigationBarsPadding()
    private val backgroundColor = Color(158, 158, 158, 255)

    @Composable
    fun CompareCardSurface(content: @Composable () -> Unit) {
        Surface(
            modifier = surfaceModifier,
            shape = RectangleShape,
            color = backgroundColor,
        ) {
            content()
        }
    }
}