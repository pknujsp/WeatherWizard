package io.github.pknujsp.everyweather.feature.permoptimize.feature

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.common.StatefulFeature
import io.github.pknujsp.everyweather.core.ui.button.PrimaryButton

@Composable
fun UnavailableFeatureScreen(featureType: StatefulFeature, onClick: () -> Unit) {
    FailedScreen(title = featureType.title,
        alertMessage = featureType.message,
        actionMessage = featureType.action,
        reason = if (featureType is FeatureType<*>) featureType.reason else null,
        onClick = onClick)
}

@Composable
fun FailedScreen(
    @StringRes title: Int, @StringRes alertMessage: Int, @StringRes actionMessage: Int, @StringRes reason: Int? = null, onClick: () -> Unit
) {
    Column(modifier = Modifier
        .padding(horizontal = 16.dp)
        .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)) {
        Text(text = stringResource(title), style = TextStyle(fontSize = 24.sp, color = Color.Black))
        Text(text = stringResource(alertMessage), style = TextStyle(fontSize = 16.sp, color = Color.DarkGray))
        if (reason != null) {
            Text(text = stringResource(reason), style = TextStyle(fontSize = 16.sp, color = Color.DarkGray))
        }
        PrimaryButton(text = stringResource(id = actionMessage), onClick = {
            onClick()
        }, modifier = Modifier.align(Alignment.End))
    }
}