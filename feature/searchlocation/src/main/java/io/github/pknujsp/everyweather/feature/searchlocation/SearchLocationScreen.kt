package io.github.pknujsp.everyweather.feature.searchlocation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.everyweather.core.ui.TitleTextWithNavigation
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.feature.favorite.search.SearchBar
import io.github.pknujsp.everyweather.feature.favorite.search.SearchResultScreen

@Composable
fun SearchLocationScreen(
    searchAreaViewModel: SearchLocationViewModel = hiltViewModel(), onSelectedLocation: (PickedLocation?) -> Unit, popBackStack: () -> Unit
) {
    BackHandler {
        popBackStack()
    }
    Column(modifier = Modifier.fillMaxSize()) {
        val searchResult by searchAreaViewModel.searchResult.collectAsStateWithLifecycle()
        val uiAction by searchAreaViewModel.uiAction.collectAsStateWithLifecycle()

        val query by remember { mutableStateOf("" to 0L) }
        var showSearchHistory by remember { mutableStateOf(true) }

        LaunchedEffect(uiAction) {
            uiAction.onOnSelectedArea {
                onSelectedLocation(it)
                popBackStack()
            }
        }

        TitleTextWithNavigation(title = stringResource(id = R.string.add_new_area)) {
            if (showSearchHistory) {
                popBackStack()
            } else {
                showSearchHistory = false
            }
        }
        SearchBar(Modifier.padding(horizontal = 16.dp), query, onChangeQuery = {
            if (it.isEmpty()) {
                showSearchHistory = true
            }
        }) {
            showSearchHistory = false
            searchAreaViewModel.search(it)
        }

        if (!showSearchHistory) {
            SearchResultScreen(searchResult) {
                showSearchHistory = true
            }
        }
    }
}