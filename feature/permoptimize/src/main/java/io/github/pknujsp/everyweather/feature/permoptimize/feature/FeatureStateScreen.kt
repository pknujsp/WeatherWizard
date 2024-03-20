package io.github.pknujsp.everyweather.feature.permoptimize.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pknujsp.everyweather.core.common.StatefulFeature
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.button.ButtonSize
import io.github.pknujsp.everyweather.core.ui.button.PrimaryButton
import io.github.pknujsp.everyweather.core.ui.button.SecondaryButton
import io.github.pknujsp.everyweather.feature.permoptimize.BaseFeatureStateManager

@Composable
fun FeatureStateScreen(featureStateManager: BaseFeatureStateManager) {
    if (!featureStateManager.featureType.isEnabled(LocalContext.current)) {
        Box {
            UnavailableFeatureScreen(featureType = featureStateManager.featureType) {
                featureStateManager.showSettingsActivity()
            }
            if (featureStateManager.isShowSettingsActivity) {
                ShowSettingsActivity(featureStateManager.featureType) {
                    featureStateManager.hideSettingsActivity()
                }
            }
        }
    }
}

@Composable
fun SmallFeatureStateScreen(
        modifier: Modifier = Modifier,
        state: StatefulFeature,
        onClickRetry: (() -> Unit)? = null,
        onClickAction: (() -> Unit)? = null,
) {
    Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Text(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(id = state.message),
                style = TextStyle(fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Left),
        )
        if (state.reason != null) {
            Text(
                    modifier = Modifier.align(Alignment.Start),
                    text = stringResource(id = state.reason!!),
                    style = TextStyle(fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Left),
            )
        }
        Row(modifier = Modifier.align(Alignment.End), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
            if (state.hasRepairAction) {
                SecondaryButton(text = stringResource(id = state.action), buttonSize = ButtonSize.SMALL) {
                    onClickAction?.invoke()
                }
            }
            if (state.hasRetryAction) {
                PrimaryButton(text = stringResource(id = R.string.reload), buttonSize = ButtonSize.SMALL) {
                    onClickRetry?.invoke()
                }
            }
        }
    }
}