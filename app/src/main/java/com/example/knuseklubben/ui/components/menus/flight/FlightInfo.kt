package com.example.knuseklubben.ui.components.menus.flight

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.knuseklubben.R
import com.example.knuseklubben.ui.components.menus.getLayoutSizes
import com.example.knuseklubben.ui.screens.AirportSelectionButtons
import com.example.knuseklubben.ui.screens.MainViewModel
import java.util.Calendar
import java.util.TimeZone

/**
 * Displays the flight information in a composable UI.
 * @param mainViewModel The MainViewModel instance responsible for managing the state and
 * data for the flight information.
 */
@Composable
fun FlightInfo(mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val layout = getLayoutSizes(14, 0, 0)
    val flight = mainViewModel.chosenFlightToShow.collectAsState().value

    val departure = flight?.firstSeen
    val arrival = flight?.lastSeen
    val departureTime = departure?.let { getFormattedDateString(it) }
    val arrivalTime = arrival?.let { getFormattedDateString(it) }
    val airline = flight?.callsign?.let { findAirline(it, context) }
    val callsign: String? = flight?.callsign

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(layout.sheetHeight.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        AirportSelectionButtons(mainViewModel)
        Row(horizontalArrangement = Arrangement.Center) {
            if (!airline.isNullOrBlank()) {
                Text(
                    text = "$airline: ",
                    modifier = Modifier.padding(end = 8.dp),
                    color = Color.Black,
                    fontSize = layout.customFont.sp
                )
            }
            if (!callsign.isNullOrBlank()) {
                Text(
                    text = callsign,
                    color = Color.Black,
                    fontSize = layout.customFont.sp
                )
            }
        }

        if (layout.sheetHeight >= 250) {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.departure), color = Color.Black, fontSize = layout.customFont.sp)
                if (departureTime.isNullOrBlank()) {
                    Text(
                        text = stringResource(R.string.unknown),
                        fontSize = layout.customFont.sp
                    )
                } else {
                    Text(
                        text = departureTime,
                        fontSize = layout.customFont.sp
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.arrival), color = Color.Black, fontSize = layout.customFont.sp)
                if (arrivalTime.isNullOrBlank()) {
                    Text(
                        text = stringResource(R.string.unknown),
                        fontSize = layout.customFont.sp
                    )
                } else {
                    Text(
                        text = arrivalTime,
                        fontSize = layout.customFont.sp
                    )
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.departure), color = Color.Black, fontSize = layout.customFont.sp)
                if (departureTime.isNullOrBlank()) {
                    Text(
                        text = stringResource(R.string.unknown),
                        fontSize = layout.customFont.sp
                    )
                } else {
                    Text(
                        text = departureTime,
                        fontSize = layout.customFont.sp
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))
                Spacer(
                    modifier = Modifier
                        .width(2.dp)
                        .height(25.dp)
                        .background(Color.Black)
                )
                Spacer(modifier = Modifier.size(10.dp))

                Text(stringResource(R.string.arrival), color = Color.Black, fontSize = layout.customFont.sp)
                if (arrivalTime.isNullOrBlank()) {
                    Text(
                        text = stringResource(R.string.unknown),
                        fontSize = layout.customFont.sp
                    )
                } else {
                    Text(
                        text = arrivalTime,
                        fontSize = layout.customFont.sp
                    )
                }
            }
        }
    }
}

/**
 * Finds and returns the airline name based on the flight number.
 * @param flightnumber The flight number used to determine the airline.
 * @param context The Context instance used for accessing string resources.
 * @return The airline name as a String, or an empty String if the flight number doesn't match any known airlines.
 */
private fun findAirline(flightnumber: String, context: Context): String {
    when {
        flightnumber.startsWith("BAW") -> {
            return context.getString(R.string.airline_british_airways)
        }

        flightnumber.startsWith("ICE") -> {
            return context.getString(R.string.airline_icelandair)
        }

        flightnumber.startsWith("KLM") -> {
            return context.getString(R.string.airline_klm)
        }

        flightnumber.startsWith("NOZ") -> {
            return context.getString(R.string.airline_norwegian)
        }

        flightnumber.startsWith("SAS") -> {
            return context.getString(R.string.airline_sas)
        }

        flightnumber.startsWith("THY") -> {
            return context.getString(R.string.airline_turkish_airlines)
        }

        flightnumber.startsWith("UAL") -> {
            return context.getString(R.string.airline_united_airlines)
        }

        flightnumber.startsWith("WIF") -> {
            return context.getString(R.string.airline_wideroe)
        }

        else -> {
            return ""
        }
    }
}

/**
 * Converts a Unix timestamp to a formatted date string.
 * @param unixTime The Unix timestamp representing the time to be formatted.
 * @return The formatted date string in the format "Weekday Day/Month-Hour:Minute".
 */
fun getFormattedDateString(unixTime: Long): String {
    val text: String
    val date = Calendar.getInstance()
    date.timeZone = TimeZone.getTimeZone("Europe/Oslo")
    date.timeInMillis = unixTime * 1000

    val days = listOf("Søndag", "Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag")
    /* Plus 1 because calendar uses zero-index counting of months, but we want it
        in a readable format i.e January having a value of 1 instead of 0 */
    val month = date.get(Calendar.MONTH) + 1
    /* For removing the leading zero in months that should only have one
       digit i.e 05 becomes 5 */
    val correctedMonth = month.toString().toInt().toString()

    text = String.format(
        "%s %d/%s-%02d:%02d",
        days[date.get(Calendar.DAY_OF_WEEK) - 1],
        date.get(Calendar.DAY_OF_MONTH),
        correctedMonth,
        date.get(Calendar.HOUR_OF_DAY),
        date.get(Calendar.MINUTE)
    )
    return text
}
