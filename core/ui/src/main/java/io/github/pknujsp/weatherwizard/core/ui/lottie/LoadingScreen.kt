package io.github.pknujsp.weatherwizard.core.ui.lottie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import io.github.pknujsp.weatherwizard.core.ui.ButtonSize
import io.github.pknujsp.weatherwizard.core.ui.PrimaryButton
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingScreen(text: String? = null, onDismissRequest: (() -> Unit)? = null, content: @Composable (() -> Unit)? = null) {
    /** lottie
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_lma54law))
    val progress by animateLottieCompositionAsState(composition,
    iterations = LottieConstants.IterateForever,
    reverseOnRepeat = true,
    speed = 1.1f)
     **/
    Dialog(onDismissRequest = {
        onDismissRequest?.invoke()
    },
        properties = DialogProperties(dismissOnBackPress = false,
            dismissOnClickOutside = false,
            decorFitsSystemWindows = false)) {
        Surface(
            shape = AppShapes.extraLarge,
            color = Color.White,
        ) {
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)) {
                CircularProgressIndicator(
                    color = Color.Black,
                    trackColor = Color(0xFFD9D9D9),
                )
                text?.run {
                    Text(text = this,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 16.dp))
                }
                content?.run {
                    Spacer(modifier = Modifier.height(16.dp))
                    this()
                }
            }
        }
    }
}


@Composable
fun CancellableLoadingScreen(
    text: String? = null,
    onDismissRequest: () -> Unit,
) {
    LoadingScreen(text, onDismissRequest) {
        PrimaryButton(onClick = onDismissRequest, text = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string
            .cancel), buttonSize = ButtonSize.SMALL, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun NonCancellableLoadingScreen(
    text: String? = null,
) {
    LoadingScreen(text)
}