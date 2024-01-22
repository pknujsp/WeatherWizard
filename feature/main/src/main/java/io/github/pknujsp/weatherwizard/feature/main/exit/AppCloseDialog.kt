package io.github.pknujsp.weatherwizard.feature.main.exit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.pknujsp.weatherwizard.core.ads.AdMob
import io.github.pknujsp.weatherwizard.core.common.asActivity
import io.github.pknujsp.weatherwizard.core.ui.PrimaryButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithoutNavigation
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import io.github.pknujsp.weatherwizard.feature.main.R
import kotlinx.serialization.json.JsonNull.content


@Composable
fun AppCloseDialog(onDismissRequest: () -> Unit) {
    val activity = LocalContext.current.asActivity()!!

    Dialog(onDismissRequest = {
        onDismissRequest()
    }) {
        Surface(
            shape = AppShapes.extraLarge,
            color = Color.White,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(24.dp)) {
                TitleTextWithoutNavigation(title = stringResource(io.github.pknujsp.weatherwizard.core.resource.R.string.app_close_title))
                Text(
                    text = stringResource(io.github.pknujsp.weatherwizard.core.resource.R.string.app_close_message),
                    fontSize = 16.sp,
                )
                AdMob.NativeAd()
                PrimaryButton(modifier = Modifier.align(Alignment.End),
                    text = stringResource(io.github.pknujsp.weatherwizard.core.resource.R.string.close_app)) {
                    activity.finish()
                }
            }
        }
    }
}