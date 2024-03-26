package io.github.pknujsp.everyweather.core.ui.lottie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.button.ButtonSize
import io.github.pknujsp.everyweather.core.ui.button.PrimaryButton
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingDialog(
    text: String? = null,
    onDismissRequest: (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
) {
    /** lottie
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_lma54law))
    val progress by animateLottieCompositionAsState(composition,
    iterations = LottieConstants.IterateForever,
    reverseOnRepeat = true,
    speed = 1.1f)
     **/
    Dialog(
        onDismissRequest = {
            onDismissRequest?.invoke()
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        Surface(
            shape = AppShapes.extraLarge,
            color = Color.White,
        ) {
            LoadingContent(text, content)
        }
    }
}

@Composable
fun CancellableLoadingDialog(
    text: String? = null,
    onDismissRequest: () -> Unit,
) {
    LoadingDialog(text, onDismissRequest) {
        PrimaryButton(
            onClick = onDismissRequest,
            text = stringResource(id = R.string.cancel),
            buttonSize = ButtonSize.SMALL,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun CancellableLoadingScreen(
    text: String? = null,
    onDismissRequest: () -> Unit,
) {
    LoadingContent(text) {
        PrimaryButton(
            onClick = onDismissRequest,
            text = stringResource(id = R.string.cancel),
            buttonSize = ButtonSize.SMALL,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun LoadingContent(text: String? = null, content: @Composable (() -> Unit)? = null) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
    ) {
        CircularProgressIndicator(
            color = Color.Black,
            trackColor = Color(0xFFD9D9D9),
        )
        text?.run {
            Text(text = this, fontSize = 16.sp)
        }
        content?.invoke()
    }
}

@Composable
fun NonCancellableLoadingScreen(text: String? = null) {
    LoadingDialog(text)
}