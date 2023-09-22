package io.github.pknujsp.weatherwizard.core.ui.lottie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import io.github.pknujsp.weatherwizard.core.ui.R

@Composable
private fun LoadingScreen(text: String? = null, onDismissRequest: (() -> Unit)? = null, content: @Composable (() -> Unit)? = null) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_lma54law))
    val progress by animateLottieCompositionAsState(composition,
        iterations = LottieConstants.IterateForever,
        reverseOnRepeat = true,
        speed = 1.1f)

    Dialog(onDismissRequest = {
        onDismissRequest?.invoke()
    },
        properties = DialogProperties(dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true)) {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()) {
            LottieAnimation(
                modifier = Modifier
                    .scale(2.5f)
                    .height(70.dp)
                    .align(Alignment.CenterHorizontally),
                composition = composition,
                progress = { progress },
                contentScale = ContentScale.FillHeight,
            )
            text?.run {
                Text(text = this, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            content?.run {
                Spacer(modifier = Modifier.height(16.dp))
                this()
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
        TextButton(
            onClick = { onDismissRequest() },
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White, disabledContentColor = Color.White),
        ) {
            Text(text = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.cancel))
        }
    }
}

@Composable
fun NonCancellableLoadingScreen(
    text: String? = null,
    onDismissRequest: () -> Unit,
) {
    LoadingScreen(text, onDismissRequest)
}