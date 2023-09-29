package com.example.knuseklubben.ui.components.searchbar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.knuseklubben.R
import com.example.knuseklubben.data.model.Airport
import com.example.knuseklubben.data.model.Flight
import com.example.knuseklubben.ui.components.menus.getLayoutSizes
import com.example.knuseklubben.ui.screens.MainMenus
import com.example.knuseklubben.ui.screens.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    mainViewModel: MainViewModel
) {
    val searchState = mainViewModel.searchUiState.collectAsState().value
    val textSearch = searchState.textSearch
    val flights = searchState.filteredFlights
    val airports = searchState.filteredAirports

    val itemsList = mutableListOf<Any>()
    itemsList.addAll(flights)
    itemsList.addAll(airports)

    val showList: Boolean = (textSearch != "" && itemsList.isNotEmpty())
    mainViewModel.updateHideIconsTo(showList)
    val layout = getLayoutSizes(0, 0, 33)
    val searchbarWidth: Int
    val searchbarListSize: Int
    if (layout.sheetHeight == 250) {
        searchbarListSize = 133
        searchbarWidth = 400
    } else if (layout.sheetHeight > 310) {
        searchbarListSize = 133
        searchbarWidth = 600
    } else {
        searchbarListSize = 66
        searchbarWidth = 500
    }

    Column(
        modifier = Modifier.padding(top = 16.dp, start = 19.dp, end = 19.dp, bottom = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = colorResource(R.color.beige_filling),
                modifier = Modifier
                    .width(searchbarWidth.dp)
                    .height(55.dp),
                shape = if (showList) {
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp,
                    )
                } else {
                    RoundedCornerShape(16.dp)
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(start = 7.dp)
                        .fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.icon_search),
                        tint = colorResource(R.color.dark_brown_outer_stroke),
                        modifier = Modifier.size(33.dp),
                    )
                    TextField(
                        value = textSearch,
                        onValueChange = mainViewModel::onTextSearchChange,
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxSize(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = colorResource(R.color.beige_filling),
                            focusedLabelColor = Color.Black,
                            cursorColor = colorResource(R.color.dark_brown_outer_stroke),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.searchbar_text),
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        },
                    )
                    IconButton(onClick = { mainViewModel.emptyText() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.remove_text_button),
                            tint = colorResource(R.color.dark_brown_outer_stroke),
                            modifier = Modifier.size(33.dp)
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (showList) {
                LazyColumn(
                    modifier = if ((itemsList.size) > 3) {
                        (Modifier.height(searchbarListSize.dp))
                    } else {
                        Modifier
                    }
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp,
                            )
                        )
                        .background(colorResource(R.color.beige_filling))
                        .width(searchbarWidth.dp)
                        .padding(bottom = 10.dp),
                ) {
                    if (itemsList.isNotEmpty()) {
                        items(itemsList) { item ->
                            AutocompleteSuggestion(
                                objectToShow = item,
                                mainViewModel = mainViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * This composable gives one autocomplete suggestion based on user input.
 * Each suggestion is clickable, so that the user may navigate to the chosen object
 * through the search bar when the mapViewModel updates its variable to this object.
 */
@Composable
fun AutocompleteSuggestion(
    objectToShow: Any,
    mainViewModel: MainViewModel
) {
    val layout = getLayoutSizes(customFont = 0, customButton = 0, image = 0)
    val localFocusManager = LocalFocusManager.current
    val mainMenu = when (objectToShow) {
        is Flight -> MainMenus.FLIGHTINFO
        is Airport -> MainMenus.AIRPORTOVERVIEW
        else -> MainMenus.NOTHING
    }
    val text = when (objectToShow) {
        is Flight -> "${objectToShow.callsign ?: stringResource(R.string.unknown)}: " +
            "${objectToShow.departureAirport ?: stringResource(R.string.unknown)} - " +
            (objectToShow.arrivalAirport ?: stringResource(R.string.unknown))
        is Airport -> objectToShow.name.replace("Airport", "Lufthavn")
        else -> ""
    }

    Button(
        onClick = {
            mainViewModel.resetState()
            mainViewModel.navigateForwardToElement(objectToShow, mainMenu, layout)
            mainViewModel.toggleSheetState(true)
            mainViewModel.onTextSearchChange(text)
            mainViewModel.updateMapFocus(objectToShow)
            localFocusManager.clearFocus()
        },
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .height(37.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.beige_filling),
            contentColor = colorResource(R.color.dark_brown_outer_stroke)
        ),
        content = {
            Text(
                modifier = Modifier.weight(1f),
                text = text,
            )
        }
    )
}
