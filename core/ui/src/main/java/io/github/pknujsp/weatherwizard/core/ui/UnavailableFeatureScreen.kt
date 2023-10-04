package io.github.pknujsp.weatherwizard.core.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import io.github.pknujsp.weatherwizard.core.common.UnavailableFeature

@Composable
fun UnavailableFeatureScreen(@StringRes title: Int, unavailableFeature: UnavailableFeature, onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 24.dp), verticalArrangement = Arrangement.Center) {
        Text(text = stringResource(title), style = TextStyle(fontSize = 24.sp, color = Color.Black))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(unavailableFeature.message), style = TextStyle(fontSize = 16.sp, color = Color.DarkGray))
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(text = stringResource(id = unavailableFeature.action), onClick = onClick, modifier = Modifier.align(Alignment.End))
    }
}