package com.example.knuseklubben.ui.components.menus.flight

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.knuseklubben.R
import com.example.knuseklubben.data.model.weatherIconMap
import com.example.knuseklubben.ui.components.menus.airport.AirportUiState
import com.example.knuseklubben.ui.components.menus.getLayoutSizes
import com.example.knuseklubben.ui.screens.AirportSelectionButtons
import com.example.knuseklubben.ui.screens.MainViewModel

@Composable
fun FlightWeatherMenu(mainViewModel: MainViewModel) {
    val layout = getLayoutSizes(14, 0, 90)
    var temp by remember { mutableStateOf<Double?>(null) }
    var wind by remember { mutableStateOf<Double?>(null) }
    var rain by remember { mutableStateOf<Double?>(null) }
    var symbol by remember { mutableStateOf("question_mark") }
    lateinit var airportstate: State<AirportUiState>

    mainViewModel.chosenFlightToShow.collectAsState().value?.let {
        temp = it.flightWeather?.temperature
        rain = it.flightWeather?.precipitation
        wind = it.flightWeather?.windSpeed
        symbol = it.flightWeather?.symbol ?: "question_mark"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(layout.sheetHeight.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (layout.sheetHeight > 260) {
            Spacer(modifier = Modifier.size(50.dp))
        }
        AirportSelectionButtons(mainViewModel)
        // switch with icon
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (layout.sheetHeight > 260) {
                Spacer(modifier = Modifier.size(50.dp))
            }
            Text(
                modifier = Modifier.padding(5.dp),
                text = stringResource(R.string.weather_under_flight),
                color = Color.Black,
                fontSize = layout.customFont.sp
            )
            if (layout.sheetHeight > 260) {
                Spacer(modifier = Modifier.size(100.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Image(
                    painter = painterResource(weatherIconMap[symbol] ?: R.drawable.question),
                    contentDescription = symbol,
                    Modifier.size(layout.customImage.dp)
                )
                Column(
                    modifier = Modifier.padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    when (temp) {
                        null -> Text(stringResource(R.string.no_temperature))
                        else -> Text("$temp \u2103", fontSize = layout.customFont.sp)
                    }
                    when (wind) {
                        null -> Text(stringResource(R.string.no_windstrength))
                        else -> Text(
                            "$wind" + stringResource(R.string.windstrength_measurement_ms),
                            fontSize = layout.customFont.sp
                        )
                    }
                    if (rain != null) {
                        Row {
                            Text(
                                modifier = Modifier.padding(top = 5.dp, end = 5.dp),
                                text = stringResource(R.string.rainfall),
                                fontSize = layout.customFont.sp
                            )
                            Text(
                                modifier = Modifier.padding(top = 5.dp),
                                text = "$rain" + stringResource(R.string.rain_measurement_mm),
                                fontSize = layout.customFont.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
