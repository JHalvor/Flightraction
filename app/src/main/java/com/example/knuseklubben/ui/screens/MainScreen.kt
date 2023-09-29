package com.example.knuseklubben.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.knuseklubben.R
import com.example.knuseklubben.data.model.Airport
import com.example.knuseklubben.ui.components.menus.airport.AirportWeatherMenu
import com.example.knuseklubben.ui.components.menus.airport.AirportmenuOverview
import com.example.knuseklubben.ui.components.menus.airport.DepartureArrivals
import com.example.knuseklubben.ui.components.menus.airport.Travel
import com.example.knuseklubben.ui.components.menus.flight.FlightInfo
import com.example.knuseklubben.ui.components.menus.flight.FlightWeatherMenu
import com.example.knuseklubben.ui.components.menus.getLayoutSizes
import com.example.knuseklubben.ui.components.menus.place.GetPlaceAttractions
import com.example.knuseklubben.ui.screens.map.Map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class MainMenus {
    AIRPORTOVERVIEW, AIRPORTATTRACTION, AIRPORTWEATHER, AIRPORTDEPARTURE, AIRPORTARRIVAL,
    FLIGHTINFO, FLIGHTWEATHER, NOTHING
}

/**
 * This is the main screen for the application. It represents the primary user interface
 * where the map and menu are displayed. The screen utilizes the BottomSheetScaffold
 * composable to create a collapsible menu at the bottom, while the map is positioned
 * underneath it. Based on chosen object, correct menu will show.
 * @param mainViewModel The MainViewModel instance responsible for managing the state,
 * data, and logic for the main screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val sheetState = mainViewModel.sheetState.collectAsState().value
    val airport = mainViewModel.chosenAirportToShow.collectAsState().value
    val layout = getLayoutSizes(customFont = 0, customButton = 0, image = 0)
    val mainscreen by mainViewModel.whatMenuToShow.collectAsState()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContainerColor = colorResource(R.color.beige_filling),
        sheetPeekHeight = if (mainscreen == MainMenus.NOTHING) {
            0.dp
        } else {
            70.dp
        },
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetDragHandle = {},
        sheetSwipeEnabled = false,
        content = {
            Map(model = mainViewModel, layout)
            toggleBottomMenuShow(scope, sheetState, scaffoldState)
        },
        sheetContent = {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 5.dp, start = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (mainViewModel.stackWithEarlierObjects.value.size > 1) {
                        CircleImageButton(
                            imageName = R.drawable.backarrow_for_menu,
                            onClick = { mainViewModel.navigateBackward() },
                            rotationCondition = false,
                            description = stringResource(R.string.back_button)
                        )
                    } else {
                        Box(Modifier.size(48.dp))
                    }

                    CircleImageButton(
                        imageName = R.drawable.arrow_for_menu,
                        onClick = {
                            mainViewModel.toggleSheetState()
                            toggleBottomMenuShow(scope, sheetState, scaffoldState)
                        },
                        rotationCondition = !sheetState,
                        description = stringResource(R.string.arrow_button)
                    )

                    CircleImageButton(
                        imageName = R.drawable.exit_for_menu,
                        onClick = {
                            mainViewModel.resetZoomOnMap()
                            mainViewModel.resetState()
                            mainViewModel.emptyText()
                            mainViewModel.resetFocusObject()
                        },
                        rotationCondition = false,
                        description = stringResource(R.string.exit_button)
                    )
                }

                when (mainscreen) {
                    MainMenus.FLIGHTINFO -> {
                        mainViewModel.chosenFlightToShow.collectAsState().value?.let {
                            mainViewModel.onClickedFlight(
                                it
                            )
                        }
                        FlightInfo(mainViewModel = mainViewModel)
                    }

                    MainMenus.AIRPORTOVERVIEW -> {

                        mainViewModel.chosenAirportToShow.collectAsState().value?.let {
                            mainViewModel.onClickedAirport(
                                it
                            )
                        }
                        AirportmenuOverview(mainViewModel)
                    }

                    MainMenus.AIRPORTATTRACTION -> {
                        airport?.let {
                            GetPlaceAttractions(
                                mainViewModel,
                                it
                            )
                        }
                    }

                    MainMenus.FLIGHTWEATHER -> {
                        FlightWeatherMenu(mainViewModel)
                    }

                    MainMenus.AIRPORTWEATHER -> {
                        AirportWeatherMenu(mainViewModel)
                    }

                    MainMenus.AIRPORTDEPARTURE -> {
                        DepartureArrivals(mainViewModel, travel = Travel.DEPARTURE)
                    }

                    MainMenus.AIRPORTARRIVAL -> {
                        DepartureArrivals(mainViewModel, travel = Travel.ARRIVAL)
                    }

                    MainMenus.NOTHING -> {}
                }
            }
            Spacer(modifier = Modifier.padding(5.dp))
        }
    )
}

@Composable
fun CircleImageButton(
    imageName: Int,
    onClick: () -> Unit,
    rotationCondition: Boolean,
    description: String
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Image(
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer {
                    rotationZ = if (rotationCondition) {
                        180.0F
                    } else {
                        0.0F
                    }
                },
            painter = painterResource(id = imageName), contentDescription = description
        )
    }
}

@Composable
fun AirportSelectionButtons(mainViewModel: MainViewModel) {
    val flight = mainViewModel.chosenFlightToShow.collectAsState().value
    val layout = getLayoutSizes(15, 0, 0)

    @Composable
    fun SelectionButton(airport: Airport?) {
        Button(
            shape = RectangleShape,
            border = BorderStroke(1.dp, colorResource(R.color.dark_brown_outer_stroke)),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.beige_filling),
                contentColor = colorResource(R.color.dark_brown_outer_stroke),
                disabledContentColor = colorResource(R.color.dark_brown_outer_stroke)
            ),
            enabled = airport?.name != null,
            onClick = {
                if (airport != null) {
                    // Navigates to the selected airport
                    mainViewModel.navigateForwardToElement(
                        airport,
                        MainMenus.AIRPORTOVERVIEW,
                        layout
                    )
                }
            },
        ) {
            Text(
                text = airport?.name ?: stringResource(R.string.unknown_foreign_airport),
                fontSize = layout.customFont.sp
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (layout.sheetHeight == 250) {

            SelectionButton(
                airport = flight?.departureAirport?.let {
                    mainViewModel.getAirportBasedOnICAOCode(
                        it
                    )
                }
            )

            Text(stringResource(R.string.to))
            SelectionButton(
                airport = flight?.arrivalAirport?.let {
                    mainViewModel.getAirportBasedOnICAOCode(
                        it
                    )
                }
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                SelectionButton(
                    airport = flight?.departureAirport?.let {
                        mainViewModel.getAirportBasedOnICAOCode(
                            it
                        )
                    }
                )
                Spacer(modifier = Modifier.size(25.dp))
                Text(stringResource(R.string.to))
                Spacer(modifier = Modifier.size(25.dp))
                SelectionButton(
                    airport = flight?.arrivalAirport?.let {
                        mainViewModel.getAirportBasedOnICAOCode(
                            it
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun toggleBottomMenuShow(
    scope: CoroutineScope,
    sheetState: Boolean,
    scaffoldState: BottomSheetScaffoldState,
) {
    scope.launch {
        if (!sheetState) {
            scaffoldState.bottomSheetState.partialExpand()
        } else {
            scaffoldState.bottomSheetState.expand()
        }
    }
}

@Composable
fun MenuTopBar(menutype: String, placeName: String?) {
    val layout = getLayoutSizes(18, 0, 0)
    Text(
        textAlign = TextAlign.Center,
        text = menutype.uppercase(),
        fontSize = layout.customFont.sp,
        color = colorResource(R.color.dark_brown_outer_stroke),
    )
    Text(
        modifier = Modifier.padding(bottom = 6.dp),
        textAlign = TextAlign.Center,
        text = placeName?.replace("Airport", "Lufthavn")
            ?: stringResource(R.string.unknown_foreign_airport),
        fontSize = layout.customFont.sp,
        color = colorResource(R.color.dark_brown_outer_stroke)
    )
}

@Composable
fun ShowErrorTextForApi() {
    val layout = getLayoutSizes(customFont = 0, customButton = 0, image = 0)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(layout.sheetHeight.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.loading_error), modifier = Modifier.padding(100.dp))
    }
}

@Composable
fun ShowLoadingTextForApi() {
    val layout = getLayoutSizes(customFont = 0, customButton = 0, image = 0)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(layout.sheetHeight.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.loading), modifier = Modifier.padding(100.dp))
    }
}
