package io.github.pknujsp.weatherwizard.feature.componentservice.widget

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.asActivity
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.ButtonSize
import io.github.pknujsp.weatherwizard.core.ui.PrimaryButton
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.ThirdButton
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.weatherwizard.feature.componentservice.AppComponentServiceReceiver

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
fun WidgetDialogScreen() {
    Box(contentAlignment = androidx.compose.ui.Alignment.Center, modifier = Modifier.padding(24.dp, 24.dp)) {
        val activity = LocalContext.current.asActivity()!!
        Content(onClickLaunchApp = {
            startActivity(activity, ComponentPendingIntentManager.mainActivityIntent, null)
            activity.finish()
        }, onClickRefresh = {
            Intent(activity, AppComponentServiceReceiver::class.java).run {
                action = AppComponentServiceReceiver.ACTION_PROCESS
                putExtras(LoadWidgetDataArgument(LoadWidgetDataArgument.UPDATE_ALL).toBundle())
                activity.sendBroadcast(this)
            }
            activity.finish()
        }, onClickCancel = {
            activity.finish()
        })
    }
}


@Composable
fun Content(onClickLaunchApp: () -> Unit, onClickRefresh: () -> Unit, onClickCancel: () -> Unit) {
    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(R.drawable.textlogo_small).build(),
            contentDescription = null,
            modifier = Modifier.height(24.dp))
        PrimaryButton(text = stringResource(id = R.string.launch_app), buttonSize = ButtonSize.MEDIUM, modifier = Modifier.fillMaxWidth()) {
            onClickLaunchApp()
        }
        SecondaryButton(text = stringResource(id = R.string.refresh), buttonSize = ButtonSize.MEDIUM, modifier = Modifier.fillMaxWidth()) {
            onClickRefresh()
        }
        ThirdButton(text = stringResource(id = R.string.close), buttonSize = ButtonSize.SMALL, modifier = Modifier.fillMaxWidth()) {
            onClickCancel()
        }
    }
}