package io.github.pknujsp.weatherwizard.feature.widget.activity

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import io.github.pknujsp.weatherwizard.core.ui.MediumTitleTextWithoutNavigation
import io.github.pknujsp.weatherwizard.core.ui.PrimaryButton
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.ThirdButton
import io.github.pknujsp.weatherwizard.feature.widget.R
import io.github.pknujsp.weatherwizard.feature.widget.WidgetManager
import io.github.pknujsp.weatherwizard.feature.widget.summary.SummaryWeatherWidgetProvider


@Composable
fun WidgetDialogScreen() {

    Surface(color = Color.White,
        shape = io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes.extraLarge,
        modifier = Modifier.wrapContentSize()) {
        Box(contentAlignment = androidx.compose.ui.Alignment.Center, modifier = Modifier.wrapContentSize()) {
            val context = LocalContext.current
            val activity = context as Activity

            Content(onClickLaunchApp = {
                val intent = Intent(context, io.github.pknujsp.weatherwizard.feature.main.MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(context, intent, null)
                activity.finish()
            }, onClickRefresh = {
                WidgetManager.getInstance(context).getUpdatePendingIntent(context, WidgetManager.Action.UPDATE_ALL_WIDGETS).send()
            }, onClickCancel = {
                activity.finish()
            })
        }
    }
}


@Composable
fun Content(onClickLaunchApp: () -> Unit = {}, onClickRefresh: () -> Unit = {}, onClickCancel: () -> Unit = {}) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 48.dp, vertical = 36.dp)) {
        MediumTitleTextWithoutNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.app_name))
        PrimaryButton(text = stringResource(id = R.string.launch_app)) {
            onClickLaunchApp()
        }
        SecondaryButton(text = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.refresh)) {
            onClickRefresh()
        }
        ThirdButton(text = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.cancel)) {
            onClickCancel()
        }
    }
}