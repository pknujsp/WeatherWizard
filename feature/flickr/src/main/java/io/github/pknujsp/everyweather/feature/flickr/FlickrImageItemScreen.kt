package io.github.pknujsp.everyweather.feature.flickr

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.theme.outlineTextStyle

@Composable
fun FlickrImageItemScreen(
    requestParameter: FlickrRequestParameters,
    onImageUrlChanged: (String) -> Unit,
    viewModel: FlickrImageViewModel = hiltViewModel(),
) {
    LaunchedEffect(requestParameter) {
        viewModel.initialize(requestParameter)
    }

    val onImageUrlChangedState by rememberUpdatedState(newValue = onImageUrlChanged)
    val uiState = viewModel.flickrImageUiState
    val context = LocalContext.current
    LaunchedEffect(uiState) {
        onImageUrlChangedState(if (uiState.isLoaded) uiState.url else "")
    }

    UrlItem(isSuccess = uiState.isLoaded, text = if (uiState.isLoaded) uiState.url else stringResource(uiState.textRes), onClickUrl = {
        if (uiState.isLoaded) {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(context, uiState.url.toUri())
        }
    }, onClickRefresh = {
        if (!uiState.isLoading) viewModel.load()
    })
}

@Composable
private fun UrlItem(isSuccess: Boolean, text: String, onClickUrl: () -> Unit, onClickRefresh: () -> Unit) {
    Row(modifier = Modifier
        .padding(start = 84.dp, end = 14.dp, top = 6.dp)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {

        Text(text = text,
            textDecoration = TextDecoration.Underline,
            color = Color.White,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = LocalTextStyle.current.merge(outlineTextStyle),
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            modifier = Modifier
                .weight(1f)
                .clickable { if (isSuccess) onClickUrl() else onClickRefresh() })
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(R.drawable.flickrlogo).crossfade(false).build(),
            contentDescription = stringResource(io.github.pknujsp.everyweather.core.resource.R.string.flickr),
            contentScale = ContentScale.Inside,
            modifier = Modifier.width(32.dp))
    }
}