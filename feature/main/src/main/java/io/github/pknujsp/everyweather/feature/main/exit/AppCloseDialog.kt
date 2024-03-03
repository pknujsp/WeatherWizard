package io.github.pknujsp.everyweather.feature.main.exit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pknujsp.everyweather.core.ads.AdMob
import io.github.pknujsp.everyweather.core.common.asActivity
import io.github.pknujsp.everyweather.core.ui.ModalBottomSheetDialog
import io.github.pknujsp.everyweather.core.ui.button.PrimaryButton


@Composable
internal fun AppCloseDialog(onDismissRequest: () -> Unit) {
    val activity = LocalContext.current.asActivity()!!
    ModalBottomSheetDialog(onDismiss = onDismissRequest,
        freeHeight = true,
        title = stringResource(io.github.pknujsp.everyweather.core.resource.R.string.app_close_title)) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = stringResource(io.github.pknujsp.everyweather.core.resource.R.string.app_close_message),
                fontSize = 16.sp,
            )
            AdMob.NativeAd(modifier = Modifier.height(IntrinsicSize.Min))
            PrimaryButton(modifier = Modifier.fillMaxWidth(),
                text = stringResource(io.github.pknujsp.everyweather.core.resource.R.string.close_app)) {
                activity.finish()
            }
        }
    }
}