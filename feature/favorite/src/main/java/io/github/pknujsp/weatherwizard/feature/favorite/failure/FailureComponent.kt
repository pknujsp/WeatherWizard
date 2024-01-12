package io.github.pknujsp.weatherwizard.feature.favorite.failure

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.ButtonSize
import io.github.pknujsp.weatherwizard.core.ui.PrimaryButton
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton

@Composable
internal fun LoadCurrentLocationFailureScreen(failedReason: FailedReason, onClickRetry: () -> Unit, onClickAction: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically), horizontalAlignment = Alignment.Start) {
        Text(text = stringResource(id = failedReason.message),
            style = TextStyle(fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Left))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
            if (failedReason.hasRepairAction) {
                SecondaryButton(text = stringResource(id = failedReason.action), buttonSize = ButtonSize.SMALL) {
                    onClickAction()
                }
            }
            PrimaryButton(text = stringResource(id = R.string.reload), buttonSize = ButtonSize.SMALL) {
                onClickRetry()
            }
        }
    }
}