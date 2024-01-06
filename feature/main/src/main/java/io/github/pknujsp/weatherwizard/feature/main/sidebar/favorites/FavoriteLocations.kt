package io.github.pknujsp.weatherwizard.feature.main.sidebar.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.theme.AppColorScheme


@Composable
fun FavoriteLocationsScreen(
    viewModel: FavoriteLocationsViewModel = hiltViewModel(), closeDrawer: () -> Unit, onClickedShowMore: () -> Unit
) {
    val favoriteLocationsUiState = viewModel.favoriteLocationsUiState
    val favoriteLocations by favoriteLocationsUiState.favoriteAreas.collectAsStateWithLifecycle()
    val targetLocation = viewModel.targetLocationUiState

    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        CurrentLocationItem(targetLocation, onClick = {
            viewModel.updateTargetLocation(SelectedLocationModel(LocationType.CurrentLocation))
            closeDrawer()
        })
        if (favoriteLocations.isEmpty()) {
            Text(text = stringResource(R.string.no_favorite_location),
                fontSize = 16.sp,
                color = AppColorScheme.primary,
                textAlign = TextAlign.Center)
        } else {
            for (item in favoriteLocations) {
                FavoriteLocationItem(item, targetLocation.locationId, onClick = {
                    viewModel.updateTargetLocation(SelectedLocationModel(LocationType.CustomLocation, item.id))
                    closeDrawer()
                })
            }
            if (favoriteLocationsUiState.containMore) {
                TextButton(onClick = { onClickedShowMore() }) {
                    Text(text = stringResource(R.string.show_more), style = TextStyle(fontSize = 14.sp, color = AppColorScheme.primary))
                }
            }
        }
    }
}


@Composable
private fun FavoriteLocationItem(
    favoriteLocation: FavoriteArea, targetLocationId: Long?, onClick: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
            Column(modifier = Modifier
                .weight(1f)
                .clickable {
                    if (targetLocationId == null || targetLocationId != favoriteLocation.id) {
                        onClick()
                    }
                }, verticalArrangement = Arrangement.Center) {
                Text(text = favoriteLocation.countryName, style = TextStyle(fontSize = 12.sp, color = Color.Gray))
                Text(text = favoriteLocation.areaName, style = TextStyle(fontSize = 14.sp, color = Color.Black))
            }
            Checkbox(checked = targetLocationId == favoriteLocation.id, onCheckedChange = {
                if (targetLocationId == null || targetLocationId != favoriteLocation.id) {
                    onClick()
                }
            })
        }
    }
}


@Composable
private fun CurrentLocationItem(
    targetLocationUiState: TargetLocationUiState, onClick: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (targetLocationUiState.locationType !is LocationType.CurrentLocation) {
                    onClick()
                }
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = stringResource(id = R.string.current_location),
                modifier = Modifier.size(18.dp),
            )
            Column(modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(id = R.string.current_location),
                    style = TextStyle(fontSize = 13.sp, color = Color.Gray),
                )

                if (targetLocationUiState.isLoading) {
                    Text(
                        text = stringResource(id = R.string.finding_current_location),
                        style = TextStyle(fontSize = 13.sp, color = Color.Black),
                    )
                } else {

                    when (val state = targetLocationUiState.loadCurrentLocationState) {
                        is LoadCurrentLocationState.Success -> {
                            Text(
                                text = state.addressName,
                                style = TextStyle(fontSize = 14.sp, color = Color.Black),
                            )
                        }

                        is LoadCurrentLocationState.Failed -> {
                            Text(
                                text = stringResource(id = state.failedReason.title),
                                style = TextStyle(fontSize = 14.sp, color = Color.Black),
                            )
                        }

                        else -> {}
                    }
                }
            }
            Checkbox(checked = targetLocationUiState.locationType is LocationType.CurrentLocation, onCheckedChange = {
                if (targetLocationUiState.locationType !is LocationType.CurrentLocation) {
                    onClick()
                }
            })
        }
    }
}