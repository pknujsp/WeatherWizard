package io.github.pknujsp.weatherwizard.feature.favorite.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.nominatim.GeoCode
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.feature.favorite.R


@Composable
fun SearchResultScreen(navController: NavController, searchResult: UiState<List<GeoCode>>) {
    Box(modifier = Modifier.fillMaxSize()) {
        searchResult.onSuccess {
            Column(modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())) {
                it.forEach { geoCode ->
                    SearchResultItem(geoCode)
                }
            }
        }.onLoading {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
            )
        }
    }
}

@Composable
fun SearchResultItem(geoCode: GeoCode) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!geoCode.isAdded) {
                    geoCode.onSelected?.invoke()
                }
            },
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterStart)
            .padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(text = geoCode.country, fontSize = 13.sp, color = Color.Gray,
                modifier = Modifier.padding(bottom = 2.dp))
            Text(text = geoCode.displayName, fontSize = 16.sp, color = Color.Black)
            if (geoCode.isAdded) {
                Text(text = stringResource(id = R.string.already_added_areas), fontSize = 12.sp, color = Color.Blue)
            }
        }
    }
}