package io.github.pknujsp.everyweather.feature.main.sidebar.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.everyweather.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.favorite.FavoriteArea
import io.github.pknujsp.everyweather.core.resource.R


private val itemPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

@Composable
fun FavoriteLocationsScreen(
    viewModel: FavoriteLocationsViewModel = hiltViewModel(), closeDrawer: () -> Unit
) {
    val favoriteLocationsUiState = viewModel.favoriteLocationsUiState
    val favoriteLocations by favoriteLocationsUiState.favoriteAreas.collectAsStateWithLifecycle()
    val targetLocation = viewModel.targetLocationUiState

    Column(modifier = Modifier.fillMaxWidth()) {
        CurrentLocationItem(targetLocation, onClick = {
            viewModel.updateTargetLocation(SelectedLocationModel(LocationType.CurrentLocation))
            closeDrawer()
        })
        if (favoriteLocations.isEmpty()) {
            Text(
                modifier = Modifier.padding(itemPadding),
                text = stringResource(R.string.no_favorite_location),
                fontSize = 14.sp,
            )
        } else {
            for (item in favoriteLocations) {
                FavoriteLocationItem(item, targetLocation.locationId, onClick = {
                    viewModel.updateTargetLocation(SelectedLocationModel(LocationType.CustomLocation, item.id))
                    closeDrawer()
                })
            }
        }
    }
}


@Composable
private fun FavoriteLocationItem(
    favoriteLocation: FavoriteArea, targetLocationId: Long?, onClick: () -> Unit
) {
    val isSelected = targetLocationId == favoriteLocation.id
    ItemBox(isSelected = isSelected, onClick = { onClick() }) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = favoriteLocation.countryName, style = TextStyle(fontSize = 12.sp, color = Color.Gray))
            Text(text = favoriteLocation.areaName, style = TextStyle(fontSize = 14.sp, color = Color.Black))
        }
    }
}


@Composable
private fun CurrentLocationItem(
    targetLocationUiState: TargetLocationUiState, onClick: () -> Unit
) {
    val isSelected = targetLocationUiState.locationType is LocationType.CurrentLocation
    ItemBox(isSelected = isSelected, onClick = { onClick() }) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = stringResource(id = R.string.current_location),
                modifier = Modifier.size(16.dp),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(id = R.string.current_location),
                    style = TextStyle(fontSize = 13.sp, color = Color.Gray),
                )
                if (!targetLocationUiState.isCurrentLocationLoading) {
                    Text(
                        text = targetLocationUiState.currentLocationAddress
                            ?: stringResource(id = targetLocationUiState.loadCurrentLocationFailedReason!!.title),
                        style = TextStyle(fontSize = 14.sp, color = Color.Black),
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemBox(isSelected: Boolean, onClick: () -> Unit, content: @Composable () -> Unit) {
    NavigationDrawerItem(label = { content() }, selected = isSelected, onClick = {
        if (!isSelected) {
            onClick()
        }
    }, colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = Color(0xA8E9E9E9)))
}