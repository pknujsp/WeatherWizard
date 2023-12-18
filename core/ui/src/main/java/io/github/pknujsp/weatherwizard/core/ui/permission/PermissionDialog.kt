package io.github.pknujsp.weatherwizard.core.ui.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.pknujsp.weatherwizard.core.ui.PrimaryButton
import io.github.pknujsp.weatherwizard.core.ui.theme.AppColorScheme


@Composable
fun PermissionDialog(onDismissRequest: () -> Unit, onGrantPermission: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)) {
        Column(
            modifier = Modifier
                .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(text = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.title_why_you_need_permissions),
                style = MaterialTheme.typography
                    .titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.description_why_you_need_permissions),
                style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                PrimaryButton(colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = AppColorScheme.primary
                ), text = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.close), onClick = onDismissRequest)
                PrimaryButton(text = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.grant_permissions),
                    onClick = onGrantPermission)
            }
        }
    }
}