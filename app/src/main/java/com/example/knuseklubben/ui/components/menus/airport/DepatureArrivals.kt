package com.example.knuseklubben.ui.components.menus.airport

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.knuseklubben.R
import com.example.knuseklubben.data.model.FlightArrivalDeparture
import com.example.knuseklubben.ui.components.menus.getLayoutSizes
import com.example.knuseklubben.ui.screens.MainViewModel
import com.example.knuseklubben.ui.screens.MenuTopBar

enum class Travel {
    DEPARTURE, ARRIVAL
}

@Composable
fun DepartureArrivals(mainViewModel: MainViewModel, travel: Travel) {
    val layout = getLayoutSizes(15, 0, 0)
    val airport = mainViewModel.chosenAirportToShow.collectAsState().value
    val airportName = airport?.name
    val scrollState = rememberScrollState(0)

    Column(
        Modifier
            .fillMaxWidth()
            .height(layout.sheetHeight.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var displayText = ""
        var flightStatus: List<FlightArrivalDeparture>? = listOf()

        when (travel) {
            Travel.ARRIVAL -> {
                displayText = stringResource(R.string.arrivals_for)
                flightStatus = airport?.arrivals
            }
            Travel.DEPARTURE -> {
                displayText = stringResource(R.string.departures_for)
                flightStatus = airport?.departures
            }
        }

        MenuTopBar(displayText, airportName)

        Text(
            text = stringResource(R.string.for_today),
            fontSize = layout.customFont.sp,
            modifier = Modifier.padding(top = 10.dp, bottom = 5.dp)
        )

        if (flightStatus.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (displayText == stringResource(R.string.arrivals_for)) {
                    Text(
                        text = stringResource(id = R.string.no_arrivals_to_show),
                        fontSize = layout.customFont.sp
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.no_departures_show),
                        fontSize = layout.customFont.sp
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .scrollable(orientation = Orientation.Vertical, state = scrollState)
                .fillMaxWidth()
        ) {
            flightStatus?.forEach {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .border(
                                BorderStroke(1.dp, color = Color.Gray),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            "${it.flightId}",
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .padding(4.dp)
                        ) {
                            Text(
                                "${it.airport}",
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                            it.via_airport?.let { it1 ->
                                Text(
                                    text = it1, modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        Text(
                            text = "${
                            it.scheduleTime?.let { it1 ->
                                formatDateString(it1)
                            }
                            }",
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

fun formatDateString(isoFormattedString: String): String {
    val time = isoFormattedString.split("T")[1]
    val timeIndexing = time.split(":")
    return "${timeIndexing[0]}:${timeIndexing[1]}"
}
