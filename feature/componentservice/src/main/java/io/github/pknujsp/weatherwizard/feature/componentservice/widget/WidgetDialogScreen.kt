package io.github.pknujsp.weatherwizard.feature.componentservice.widget

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDirection.Companion.Content
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.MediumTitleTextWithoutNavigation
import io.github.pknujsp.weatherwizard.core.ui.PrimaryButton
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.ThirdButton
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.weatherwizard.feature.componentservice.NotificationServiceReceiver

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
fun WidgetDialogScreen() {
    Box(contentAlignment = androidx.compose.ui.Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = AppShapes.extraLarge,
            shadowElevation = 8.dp,
        ) {
            val context = LocalContext.current

            Content(onClickLaunchApp = {
                context as Activity
                context.finish()
                startActivity(context, ComponentPendingIntentManager.mainActivityIntent, null)
            }, onClickRefresh = {
                Intent(context, NotificationServiceReceiver::class.java).run {
                    action = NotificationServiceReceiver.ACTION_PROCESS
                    putExtras(LoadWidgetDataArgument(LoadWidgetDataArgument.UPDATE_ALL).toBundle())
                    context.sendBroadcast(this)
                }
            }, onClickCancel = {
                context as Activity
                context.finish()
            })
        }
    }

}


@Composable
fun Content(onClickLaunchApp: () -> Unit = {}, onClickRefresh: () -> Unit = {}, onClickCancel: () -> Unit = {}) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 42.dp, vertical = 36.dp)) {
        AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(R.mipmap.ic_launcher_foreground).build(),
            contentDescription = null,
            modifier = Modifier.size(28.dp))
        PrimaryButton(text = stringResource(id = R.string.launch_app)) {
            onClickLaunchApp()
        }
        SecondaryButton(text = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.refresh)) {
            onClickRefresh()
        }
        ThirdButton(text = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.cancel)) {
            onClickCancel()
        }
    }
}