package io.github.pknujsp.weatherwizard.feature.notification.ongoing

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation


@Composable
fun OngoingNotificationScreen(navController: NavController) {
    Column {
        TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.title_ongoing_notification)) {
            navController.popBackStack()
        }
    }
}