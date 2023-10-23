package io.github.pknujsp.weatherwizard.feature.widget.activity

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import io.github.pknujsp.weatherwizard.core.ui.PrimaryButton
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.ThirdButton
import io.github.pknujsp.weatherwizard.feature.widget.R


@Composable
fun WidgetDialogScreen() {
    val context = LocalContext.current
    val activity = context as androidx.appcompat.app.AppCompatActivity

    Content(onClickLaunchApp = {
        val intent = Intent(context, io.github.pknujsp.weatherwizard.feature.main.MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(context, intent, null)
        activity.finish()
    }, onClickRefresh = {

    }, onClickCancel = {
        activity.finish()
    })
}


@Composable
fun Content(onClickLaunchApp: () -> Unit = {}, onClickRefresh: () -> Unit = {}, onClickCancel: () -> Unit = {}) {
    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(painter = painterResource(id = io.github.pknujsp.weatherwizard.core.common.R.mipmap.ic_launcher), contentDescription = null,
            modifier = Modifier.size(24.dp))
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