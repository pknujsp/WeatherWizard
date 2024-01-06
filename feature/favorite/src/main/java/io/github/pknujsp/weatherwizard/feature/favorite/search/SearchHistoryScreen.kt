package io.github.pknujsp.weatherwizard.feature.favorite.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.searchhistory.SearchHistory
import io.github.pknujsp.weatherwizard.core.ui.list.EmptyListScreen

@Composable
internal fun SearchHistoryScreen(
    viewModel: SearchHistoryViewModel = hiltViewModel(),
    onSearchHistoryItemClicked: (String) -> Unit,
) {
    val history by viewModel.history.collectAsStateWithLifecycle()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (history.isEmpty()) {
            item {
                EmptyListScreen(message = io.github.pknujsp.weatherwizard.core.resource.R.string.no_search_history)
            }
        } else {
            items(items = history, key = { history -> history.id }) { history ->
                SearchHistoryItem(history, onSearchHistoryItemClicked)
            }
        }
    }

}

@Composable
private fun SearchHistoryItem(history: SearchHistory, onSearchHistoryItemClicked: (String) -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .clickable {
            onSearchHistoryItemClicked(history.query)
        }) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text(text = history.query, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color.Black)
            IconButton(onClick = {
                history.onDeleteClicked?.invoke()
            }) {
                Icon(Icons.Rounded.Delete, contentDescription = stringResource(id = R.string.delete))
            }
        }
    }


}