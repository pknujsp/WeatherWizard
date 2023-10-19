package io.github.pknujsp.weatherwizard.feature.notification.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationUiModel
import io.github.pknujsp.weatherwizard.core.model.notification.OngoingNotificationInfo
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.ui.MediumTitleTextWithoutNavigation
import io.github.pknujsp.weatherwizard.core.ui.RadioButtons
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.OngoingNotificationViewModel

@Composable
fun ColumnScope.LocationScreen(entity: NotificationUiModel, onClick: () -> Unit) {
    MediumTitleTextWithoutNavigation(title = stringResource(id = R.string.location))
    val radioOptions = remember { LocationType.enums }
    var selectedOption by remember { mutableStateOf(entity.locationType) }

    RadioButtons(radioOptions = radioOptions, selectedOption = selectedOption, onOptionSelected = {
        entity.locationType = it
        selectedOption = it
    })

    if (selectedOption is LocationType.CustomLocation) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = entity.addressName, style = TextStyle(fontSize = 16.sp))
            SecondaryButton(text = stringResource(id = io.github.pknujsp.weatherwizard.feature.notification.R.string.select_location), modifier = Modifier.wrapContentSize()) {
                onClick()
            }
        }
    }
}

@Composable
fun WeatherProvidersScreen(entity: NotificationUiModel) {
    MediumTitleTextWithoutNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.feature.notification.R.string.weather_provider))

    val radioOptions = remember { WeatherDataProvider.enums }
    var selectedOption by remember { mutableStateOf(entity.weatherProvider) }

    RadioButtons(radioOptions = radioOptions, selectedOption = selectedOption, onOptionSelected = {
        entity.weatherProvider = it
        selectedOption = it
    })
}