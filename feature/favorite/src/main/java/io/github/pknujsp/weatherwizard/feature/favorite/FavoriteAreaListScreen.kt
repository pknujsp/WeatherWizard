package io.github.pknujsp.weatherwizard.feature.favorite

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.ButtonSize
import io.github.pknujsp.weatherwizard.core.ui.MainRoutes
import io.github.pknujsp.weatherwizard.core.ui.PrimaryButton
import io.github.pknujsp.weatherwizard.core.ui.RootNavControllerViewModel
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.dialog.BottomSheet
import io.github.pknujsp.weatherwizard.core.ui.dialog.DialogScreen
import io.github.pknujsp.weatherwizard.core.ui.feature.OpenAppSettingsActivity
import io.github.pknujsp.weatherwizard.core.ui.list.EmptyListScreen
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import io.github.pknujsp.weatherwizard.feature.favorite.model.LoadCurrentLocationState
import io.github.pknujsp.weatherwizard.feature.favorite.model.TargetLocationUiState

@Composable
fun FavoriteAreaListScreen(navController: NavController, viewModel: FavoriteAreaViewModel = hiltViewModel()) {
    val favoriteLocations by viewModel.favoriteLocations.collectAsStateWithLifecycle()
    val targetLocation = viewModel.targetLocationUiState
    var showSettingsActivity by remember { mutableStateOf(false) }
    var selectedLocationToDelete by remember { mutableStateOf<FavoriteArea?>(null) }

    val rootNavControllerViewModel: RootNavControllerViewModel =
        hiltViewModel(viewModelStoreOwner = (LocalContext.current as ComponentActivity))

    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val backDispatcher = remember {
        onBackPressedDispatcherOwner?.onBackPressedDispatcher
    }

    LaunchedEffect(favoriteLocations) {
        if (favoriteLocations.isEmpty() && selectedLocationToDelete != null) {
            selectedLocationToDelete = null
        }
    }

    LaunchedEffect(targetLocation.isChanged) {
        if (targetLocation.isChanged) {
            rootNavControllerViewModel.navigate(MainRoutes.Weather)
        }
    }

    if (showSettingsActivity) {
        OpenAppSettingsActivity(featureType = (targetLocation.loadCurrentLocationState as LoadCurrentLocationState.Failed).featureType!!) {
            showSettingsActivity = false
            viewModel.loadCurrentLocation()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f), state = rememberLazyListState()) {
            item {
                TitleTextWithNavigation(title = stringResource(id = R.string.nav_favorite_areas), onClickNavigation = {
                    backDispatcher?.onBackPressed()
                })
            }
            item {
                CurrentLocationItem(targetLocation, onClick = {
                    viewModel.updateTargetLocation(SelectedLocationModel(LocationType.CurrentLocation))
                }, onClickAction = {
                    showSettingsActivity = true
                }, onClickRetry = {
                    viewModel.loadCurrentLocation()
                })
            }
            if (favoriteLocations.isEmpty()) {
                item {
                    EmptyListScreen(message = R.string.no_favorite_location)
                }
            } else {
                items(
                    items = favoriteLocations,
                    key = { item ->
                        item.id
                    },
                ) { item ->
                    FavoriteLocationItem(item, targetLocation.locationId, onClick = {
                        viewModel.updateTargetLocation(SelectedLocationModel(LocationType.CustomLocation, item.id))
                    }, onClickDelete = {
                        selectedLocationToDelete = item
                    })
                }
            }
        }

        Box(modifier = Modifier.padding(12.dp)) {
            SecondaryButton(text = stringResource(id = R.string.add_new_area), modifier = Modifier.fillMaxWidth()) {
                navController.navigate(FavoriteRoutes.AreaSearch.route)
            }
        }
    }

    val openDeleteDialog by remember {
        derivedStateOf {
            selectedLocationToDelete != null && favoriteLocations.any { it.id == selectedLocationToDelete?.id }
        }
    }
    if (openDeleteDialog) {
        DeleteDialog(address = selectedLocationToDelete!!.areaName,
            onDismissRequest = { selectedLocationToDelete = null },
            onConfirmation = {
                viewModel.deleteFavoriteLocation(selectedLocationToDelete!!.id)
                selectedLocationToDelete = null
            })
    }
}


@Composable
private fun FavoriteLocationItem(
    favoriteLocation: FavoriteArea, targetLocationId: Long?, onClick: () -> Unit, onClickDelete: (Long) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(elevation = 4.dp, shape = AppShapes.large)
            .background(color = Color.White, shape = AppShapes.large)
            .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = {
                onClickDelete(favoriteLocation.id)
            }) {
                Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
            }
            Column(modifier = Modifier
                .weight(1f)
                .clickable {
                    if (targetLocationId == null || targetLocationId != favoriteLocation.id) {
                        onClick()
                    }
                }, verticalArrangement = Arrangement.Center) {
                Text(text = favoriteLocation.countryName, style = TextStyle(fontSize = 12.sp, color = Color.Gray))
                Text(text = favoriteLocation.areaName, style = TextStyle(fontSize = 15.sp, color = Color.Black))
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
    targetLocationUiState: TargetLocationUiState, onClickRetry: () -> Unit, onClickAction: () -> Unit, onClick: () -> Unit
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
            .shadow(elevation = 4.dp, shape = AppShapes.large)
            .background(color = Color.White, shape = AppShapes.large)
            .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = stringResource(id = R.string.current_location),
                modifier = Modifier.size(24.dp),
            )
            Column(modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(id = R.string.current_location),
                    style = TextStyle(fontSize = 17.sp, color = Color.Gray),
                )

                if (targetLocationUiState.isLoading) {
                    Text(
                        text = stringResource(id = R.string.finding_current_location),
                        style = TextStyle(fontSize = 15.sp, color = Color.Black),
                    )
                } else {

                    when (val state = targetLocationUiState.loadCurrentLocationState) {
                        is LoadCurrentLocationState.Success -> {
                            Text(
                                text = state.addressName,
                                style = TextStyle(fontSize = 15.sp, color = Color.Black),
                            )
                        }

                        is LoadCurrentLocationState.Failed -> {
                            LoadCurrentLocationFailureScreen(state.failedReason ?: state.featureType!!.failedReason, onClickRetry = {
                                onClickRetry()
                            }, onClickAction = {
                                onClickAction()
                            })
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

@Composable
private fun LoadCurrentLocationFailureScreen(failedReason: FailedReason, onClickRetry: () -> Unit, onClickAction: () -> Unit) {
    val buttonSize = remember { ButtonSize.SMALL }
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
        Text(text = stringResource(id = failedReason.message),
            style = TextStyle(fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Left))
        Spacer(modifier = Modifier.size(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
            if (failedReason.hasRepairAction) {
                SecondaryButton(text = stringResource(id = failedReason.action), buttonSize = buttonSize) {
                    onClickAction()
                }
            }
            PrimaryButton(text = stringResource(id = R.string.reload), buttonSize = buttonSize) {
                onClickRetry()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteDialog(
    address: String, onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    BottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
    ) {
        val message = stringResource(id = R.string.delete_favorite_location_message).let {
            "$it\n$address"
        }
        DialogScreen(title = stringResource(id = R.string.delete_favorite_location_title),
            message = message,
            negative = stringResource(id = R.string.cancel),
            positive = stringResource(id = R.string.delete),
            onClickNegative = {
                onDismissRequest()
            },
            onClickPositive = {
                onConfirmation()
            })
    }
}