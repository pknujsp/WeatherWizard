package io.github.pknujsp.everyweather.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.everyweather.core.model.settings.IEnum
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.button.SecondaryButton
import io.github.pknujsp.everyweather.core.ui.dialog.CustomModalBottomSheet


@Composable
private fun SettingItem(
    title: String, description: String? = null, onClick: (() -> Unit)? = null, content: (@Composable () -> Unit)? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.clickable(enabled = onClick != null) {
            onClick?.invoke()
        }) {
        Column(modifier = Modifier
            .weight(1f)
            .padding(top = 12.dp, bottom = 12.dp, start = 16.dp)) {
            Text(text = title, style = TextStyle(fontSize = 16.sp, color = Color.Black))
            description?.let {
                Text(text = description, style = TextStyle(fontSize = 12.sp, color = Color.Gray))
            }
        }
        content?.run {
            invoke()
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
fun ClickableSettingItem(title: String, description: String? = null, onClick: () -> Unit) {
    SettingItem(title = title, description = description, onClick = onClick)
}

@Composable
fun CheckBoxSettingItem(title: String, description: String? = null, checked: Boolean, onClick: (Boolean) -> Unit) {
    var checkedState by remember { mutableStateOf(checked) }

    SettingItem(title = title, description = description, onClick = {
        checkedState = !checkedState
        //onClick(checkedState)
    }) {
        Checkbox(checked = checkedState, onCheckedChange = {
            checkedState = it
            //onClick(it)
        })
    }
}

@Composable
fun ButtonSettingItem(
    title: String, description: String? = null, onClick: () -> Unit, icon: (@Composable () -> Unit)? = null
) {
    SettingItem(title = title, description = description, onClick = onClick, content = icon)
}

@Composable
fun TextValueSettingItem(title: String, description: String? = null, value: () -> String, onClick: () -> Unit) {
    SettingItem(title = title, description = description, onClick = onClick) {
        Text(text = value(),
            style = TextStyle(fontSize = 15.sp, color = Color.Black, fontWeight = FontWeight.Light, textAlign = TextAlign.Right))
    }
}

@Composable
fun <E : IEnum> DropDownMenuSettingItem(
    title: String, description: String? = null, value: String, onClick: () -> Unit, onSelectedItem: (E) -> Unit, enums: Array<E>
) {
    SettingItem(title = title, description = description, onClick = onClick) {
        Text(text = value,
            style = TextStyle(fontSize = 15.sp, color = Color.Black, fontWeight = FontWeight.Light, textAlign = TextAlign.Right))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <E : IEnum> BottomSheetSettingItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    selectedItem: E,
    onSelectedItem: (E?) -> Unit,
    enums: Array<E>
) {
    var expanded by remember { mutableStateOf(false) }
    SettingItem(title = title, description = description, onClick = { expanded = true }) {
        Text(text = stringResource(id = selectedItem.title),
            style = TextStyle(fontSize = 15.sp, color = Color.Black, fontWeight = FontWeight.Light, textAlign = TextAlign.Right))
    }

    if (expanded) {
        CustomModalBottomSheet(
            onDismissRequest = {
                onSelectedItem(null)
                expanded = false
            },
        ) {
            Column {
                TitleTextWithoutNavigation(title = title)
                Column(modifier = modifier.verticalScroll(rememberScrollState(), true)) {
                    enums.forEach { enum ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier
                            .clickable {
                                onSelectedItem(enum)
                                expanded = false
                            }
                            .fillMaxWidth()) {
                            enum.icon?.let { icon ->
                                AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(icon).crossfade(false).build(),
                                    modifier = modifier
                                        .size(32.dp)
                                        .padding(start = 12.dp),
                                    contentDescription = null)
                            }
                            Text(text = stringResource(id = enum.title),
                                fontSize = 14.sp,
                                color = Color.Black,
                                modifier = modifier
                                    .weight(1f)
                                    .padding(start = 12.dp))
                            RadioButton(selected = selectedItem == enum, onClick = {
                                onSelectedItem(enum)
                                expanded = false
                            }, modifier = modifier.padding(end = 12.dp))
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun <E : IEnum> BottomSheetDialog(
    modifier: Modifier = Modifier,
    title: String,
    selectedItem: E,
    onSelectedItem: (E?) -> Unit,
    enums: Array<E>,
    expanded: () -> Boolean,
    onDismissRequest: () -> Unit
) {
    if (expanded()) {
        ModalBottomSheetDialog(
            title = title,
            onDismiss = {
                onDismissRequest()
            },
        ) {
            Column(modifier = modifier.verticalScroll(rememberScrollState(), true)) {
                enums.forEach { enum ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier
                        .clickable {
                            onSelectedItem(enum)
                            onDismissRequest()
                        }
                        .fillMaxWidth()) {
                        enum.icon?.let { icon ->
                            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(icon).crossfade(false).build(),
                                modifier = modifier
                                    .size(32.dp)
                                    .padding(start = 12.dp),
                                contentDescription = null)
                        }
                        Text(text = stringResource(id = enum.title),
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = modifier
                                .weight(1f)
                                .padding(start = 12.dp))
                        RadioButton(selected = selectedItem == enum, onClick = {
                            onSelectedItem(enum)
                            onDismissRequest()
                        }, modifier = modifier.padding(end = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun <E : IEnum> RadioButtons(
    radioOptions: Array<E>, selectedOption: E, onOptionSelected: (E) -> Unit
) {
    Column(Modifier.selectableGroup()) {
        radioOptions.forEach { option ->
            Row(Modifier
                .fillMaxWidth()
                .height(48.dp)
                .selectable(selected = (option == selectedOption), onClick = { onOptionSelected(option) }, role = Role.RadioButton)
                .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (option == selectedOption),
                    onClick = { onOptionSelected(option) },
                )
                Text(text = stringResource(option.title),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 12.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSettingItem(
    title: String,
    description: String? = null,
    currentData: String,
    isBottomSheetExpanded: Boolean,
    limitHeight: Boolean = true,
    onDismissRequest: () -> Unit,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    SettingItem(title = title, description = description, onClick = onClick) {
        Text(text = currentData,
            style = TextStyle(fontSize = 15.sp, color = Color.Black, fontWeight = FontWeight.Light, textAlign = TextAlign.Right))
    }

    if (isBottomSheetExpanded) {
        CustomModalBottomSheet(
            onDismissRequest = onDismissRequest,
        ) {
            content()
        }
    }
}

@Composable
fun ColumnScope.LocationScreen(selectedLocation: LocationTypeModel, onSelectedItem: (LocationType) -> Unit, onClick: () -> Unit) {
    MediumTitleTextWithoutNavigation(title = stringResource(id = R.string.location))

    RadioButtons(radioOptions = LocationType.enums, selectedOption = selectedLocation.locationType, onOptionSelected = {
        onSelectedItem(it)
    })

    if (selectedLocation.locationType is LocationType.CustomLocation) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.align(Alignment.End)) {
            Text(text = selectedLocation.address, style = TextStyle(fontSize = 16.sp))
            SecondaryButton(text = stringResource(id = R.string.select_location), modifier = Modifier.wrapContentSize()) {
                onClick()
            }
        }
    }
}

@Composable
fun WeatherProvidersScreen(weatherProvider: WeatherProvider, onSelectedItem: (WeatherProvider) -> Unit) {
    MediumTitleTextWithoutNavigation(title = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.weather_provider))

    RadioButtons(radioOptions = WeatherProvider.enums, selectedOption = weatherProvider, onOptionSelected = {
        onSelectedItem(it)
    })
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ModalBottomSheetDialog(
    modifier: Modifier = Modifier,
    title: String,
    freeHeight: Boolean = false,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    CustomModalBottomSheet(
        freeHeight = freeHeight,
        onDismissRequest = {
            onDismiss()
        },
    ) {
        TitleTextWithoutNavigation(title = title)
        content()
    }

}