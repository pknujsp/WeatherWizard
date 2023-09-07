package io.github.pknujsp.weatherwizard.feature.flickr

import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.weatherwizard.core.model.onError
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.theme.outlineTextStyle

@Composable
fun FlickrImageItemScreen(
    requestParameter: FlickrRequestParameters,
    onLoadedImage: (String) -> Unit
) {
    val viewModel = hiltViewModel<FlickrImageViewModel>()
    val imageUrl = rememberSaveable { mutableStateOf("") }

    viewModel.image.collectAsStateWithLifecycle().value.onSuccess {
        imageUrl.value = it.imageUrl
        if (it.imageUrl.isNotEmpty()) {
            onLoadedImage(it.imageUrl)
        }
    }.onError {
        imageUrl.value = ""
    }.onLoading {
        viewModel.load(requestParameter)
    }

    UrlItem(url = imageUrl.value, onClick = { viewModel.reload() })
}

@Composable
private fun UrlItem(url: String, onClick: () -> Unit) {
    Row(modifier = Modifier
        .padding(start = 84.dp, end = 14.dp)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {

        Text(text = url.ifEmpty { stringResource(id = R.string.reload) },
            textDecoration = TextDecoration.Underline,
            color = Color.White,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = LocalTextStyle.current.merge(outlineTextStyle),
            modifier = Modifier
                .weight(1f)
                .clickable { onClick() })
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.flickrlogo)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(io.github.pknujsp.weatherwizard.feature.flickr.R.string.flickr),
            contentScale = ContentScale.Inside,
            modifier = Modifier.width(30.dp)
        )
    }
}