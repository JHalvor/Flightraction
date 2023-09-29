package com.example.knuseklubben.ui.components.menus.airport

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.knuseklubben.R
import com.example.knuseklubben.data.model.weatherIconMap
import com.example.knuseklubben.ui.components.menus.LayoutSizes
import com.example.knuseklubben.ui.components.menus.getLayoutSizes
import com.example.knuseklubben.ui.screens.MainViewModel
import com.example.knuseklubben.ui.screens.MenuTopBar

/**
 * This composable shows the weather for chosen airport based on data from the ViewModel.
 * If some of the datas is null or blank, the user will either get an image as questionmark
 * or text saying data not availeble.
 */
@Composable
fun AirportWeatherMenu(mainViewModel: MainViewModel) {
    val airportToShow = mainViewModel.chosenAirportToShow.collectAsState()
    var temp by remember { mutableStateOf<Double?>(null) }
    var wind by remember { mutableStateOf<Double?>(null) }
    var rain by remember { mutableStateOf<Double?>(null) }
    var symbol by remember { mutableStateOf("question_mark") }
    var metar by remember { mutableStateOf("") }
    var expandMetar by remember { mutableStateOf(false) }
    val airportstate = mainViewModel.airportUiState.collectAsState()
    val scrollState = rememberScrollState(0)

    val layout = getLayoutSizes(15, 0, 100)
    val spacer = if (layout.sheetHeight > 350) {
        70
    } else {
        30
    }

    when {
        airportstate.value.airports.isNotEmpty() -> airportstate.value.airports.forEach {
            if (it.name == airportToShow.value?.name) {
                temp = it.airportWeather?.temperature
                rain = it.airportWeather?.precipitation
                wind = it.airportWeather?.windSpeed
                symbol = it.airportWeather?.symbol ?: "question_mark"
                metar = it.airportMetar ?: stringResource(R.string.no_data_available)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(layout.sheetHeight.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MenuTopBar(stringResource(R.string.forecast_for), airportToShow.value?.name)

        if (layout.sheetHeight > 230) {
            StandardWeatherForecast(symbol, layout, temp, wind, rain)
            AdvancedWeatherForecast(
                expandMetar, layout, scrollState, metar, spacer,
                onClick = { expandMetar = !expandMetar }
            )
        } else {
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StandardWeatherForecast(symbol, layout, temp, wind, rain)
                AdvancedWeatherForecast(
                    expandMetar, layout, scrollState, metar, spacer,
                    onClick = { expandMetar = !expandMetar }
                )
            }
        }
    }
}

@Composable
fun StandardWeatherForecast(
    symbol: String,
    layout: LayoutSizes,
    temp: Double?,
    wind: Double?,
    rain: Double?
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(weatherIconMap[symbol] ?: R.drawable.question),
            contentDescription = symbol,
            Modifier
                .padding(end = 50.dp)
                .size(layout.customImage.dp),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            when (temp) {
                null -> Text(
                    stringResource(R.string.no_temperature),
                    fontSize = layout.customFont.sp
                )

                else -> Text(
                    "$temp \u2103",
                    fontSize = layout.customFont.sp
                )
            }
            when (wind) {
                null -> Text(
                    stringResource(R.string.no_windstrength),
                    fontSize = layout.customFont.sp
                )

                else -> Text(
                    "$wind" + stringResource(R.string.windstrength_measurement_ms),
                    fontSize = layout.customFont.sp
                )
            }
            if (rain != null) {
                Row {

                    Text(
                        modifier = Modifier.padding(top = 6.dp, end = 5.dp),
                        text = stringResource(R.string.rainfall), fontSize = layout.customFont.sp
                    )
                    Text(
                        modifier = Modifier.padding(top = 6.dp),
                        text = "$rain" + stringResource(R.string.rain_measurement_mm),
                        fontSize = layout.customFont.sp
                    )
                }
            }
        }
    }
}

/**
 * This composable is for the more advanced forecast meant for flight traffic controller.
 */
@Composable
fun AdvancedWeatherForecast(
    expandMetar: Boolean,
    layout: LayoutSizes,
    scrollState: ScrollState,
    metar: String,
    spacer: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier.padding(top = 5.dp),
            border = BorderStroke(1.dp, colorResource(R.color.dark_brown_outer_stroke)),
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.beige_filling),
                contentColor = colorResource(R.color.dark_brown_outer_stroke)
            ),
        ) {
            Text(
                text = if (!expandMetar) {
                    stringResource(R.string.show_detailed_forecast)
                } else {
                    stringResource(R.string.hide_detailed_forecast)
                },
                fontSize = layout.fontSize.sp, textAlign = TextAlign.Center
            )
        }

        if (expandMetar) {
            LazyColumn(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp)
                    .scrollable(orientation = Orientation.Vertical, state = scrollState)
                    .width(250.dp)
                    .height(spacer.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = if (metar == "") stringResource(R.string.no_data_available)
                        else metar,
                        fontSize = layout.fontSize.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = layout.fontSize.sp * 1.2f,
                    )
                }
            }
        } else {
            // Box with the same height as the column to prevent the content from shifting position
            Box(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp)
                    .width(250.dp)
                    .height(spacer.dp)
            )
        }
    }
}
