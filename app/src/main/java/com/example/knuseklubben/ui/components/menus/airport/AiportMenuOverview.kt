package com.example.knuseklubben.ui.components.menus.airport

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.knuseklubben.R
import com.example.knuseklubben.ui.components.menus.getLayoutSizes
import com.example.knuseklubben.ui.screens.MainMenus
import com.example.knuseklubben.ui.screens.MainViewModel

/**
 * This composable makes the overview of each undermenu, represented by icons and desription
 */
@Composable
fun AirportmenuOverview(mainViewModel: MainViewModel) {
    val layout = getLayoutSizes(18, 0, 0)
    val sizes = makeOverviewSizes(mainViewModel)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(layout.sheetHeight.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        mainViewModel.chosenAirportToShow.collectAsState().value?.name?.let {
            val splitted = it.replace("Airport", "Lufthavn").split(",")
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                for (i in splitted.indices) {
                    Text(
                        text = splitted[i],
                        fontSize = layout.customFont.sp,
                        color = colorResource(R.color.dark_brown_outer_stroke)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(5.dp))
        if (layout.sheetHeight > 210) {
            Box(
                modifier = Modifier
                    .width(sizes.columnWidth.dp)
                    .height(sizes.coulmnHeight.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(sizes.rowHeight.dp),
                        Arrangement.Center
                    ) {
                        MenuButton(
                            mainViewModel,
                            text = stringResource(R.string.departures),
                            img = painterResource(id = R.drawable.icon_departure),
                            contentDescription = stringResource(R.string.departure_button),
                            menu = MainMenus.AIRPORTDEPARTURE,
                            isTop = true,
                        )

                        MenuButton(
                            mainViewModel,
                            text = stringResource(R.string.arrivals),
                            img = painterResource(id = R.drawable.icon_arrival),
                            contentDescription = stringResource(R.string.arrival_button),
                            menu = MainMenus.AIRPORTARRIVAL,
                            isTop = true,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(sizes.rowHeight.dp),
                        Arrangement.Center
                    ) {
                        MenuButton(
                            mainViewModel,
                            text = stringResource(R.string.attractions),
                            img = painterResource(id = R.drawable.icon_attraction),
                            contentDescription = stringResource(R.string.attraction_button),
                            menu = MainMenus.AIRPORTATTRACTION,
                            isTop = false,
                        )
                        MenuButton(
                            mainViewModel,
                            text = stringResource(R.string.forecast),
                            img = painterResource(id = R.drawable.icon_weather),
                            contentDescription = stringResource(R.string.weather_button),
                            menu = MainMenus.AIRPORTWEATHER,
                            isTop = false,
                        )
                    }
                }
                Image(
                    painter = painterResource(R.drawable.airport_grid),
                    contentDescription =
                    stringResource(R.string.grid_desc),
                )
            }
        } else {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(sizes.rowHeight.dp),
                Arrangement.Center
            ) {
                MenuButton(
                    mainViewModel,
                    text = stringResource(R.string.departures),
                    img = painterResource(id = R.drawable.icon_departure),
                    contentDescription = stringResource(R.string.departure_button),
                    menu = MainMenus.AIRPORTDEPARTURE,
                    isTop = true,
                )

                MenuButton(
                    mainViewModel,
                    text = stringResource(R.string.arrivals),
                    img = painterResource(id = R.drawable.icon_arrival),
                    contentDescription = stringResource(R.string.arrival_button),
                    menu = MainMenus.AIRPORTARRIVAL,
                    isTop = true,
                )
                MenuButton(
                    mainViewModel,
                    text = stringResource(R.string.attractions),
                    img = painterResource(id = R.drawable.icon_attraction),
                    contentDescription = stringResource(R.string.attraction_button),
                    menu = MainMenus.AIRPORTATTRACTION,
                    isTop = true,
                )
                MenuButton(
                    mainViewModel,
                    text = stringResource(R.string.forecast),
                    img = painterResource(id = R.drawable.icon_weather),
                    contentDescription = stringResource(R.string.weather_button),
                    menu = MainMenus.AIRPORTWEATHER,
                    isTop = true,
                )
            }
        }
    }
}

/**
 * This composable function is a helpfunction for making each button in the menu
 */
@Composable
fun MenuButton(
    mainViewModel: MainViewModel,
    text: String,
    img: Painter,
    contentDescription: String,
    menu: MainMenus,
    isTop: Boolean,
) {
    val sizes = makeOverviewSizes(mainViewModel)
    Button(
        modifier = Modifier
            .fillMaxHeight()
            .width(sizes.button.dp),
        shape = RoundedCornerShape(70.dp),
        onClick = { mainViewModel.navigateForwardToMenu(menu) },
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.beige_filling)
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isTop) {
                Image(
                    painter = img,
                    modifier = Modifier.size(sizes.image.dp),
                    contentDescription = contentDescription,
                )
                Text(
                    text = text,
                    fontSize = sizes.pictureFont.sp,
                    color = colorResource(R.color.dark_brown_outer_stroke)
                )
            } else {
                Image(
                    painter = img,
                    modifier = Modifier.size(sizes.image.dp),
                    contentDescription = contentDescription,
                )
                Text(
                    text = text,
                    fontSize = sizes.pictureFont.sp,
                    color = colorResource(R.color.dark_brown_outer_stroke)
                )
            }
        }
    }
}

/**
 * Keep sizes for the airportMenuoverview for diffrent screensizes
 */
data class OverviewSizes(
    val rowHeight: Int,
    val image: Int,
    val button: Int,
    val pictureFont: Int,
    val columnWidth: Int,
    val coulmnHeight: Int
)

/**
 * Because of the large number of composable sizes in the AiportMenuOverview
 * we made this helpfunction to adapt to the diffrent screensizes and rotated devices.
 * We decided to this rather than add a lot of variables in the Layoutsizes dataclass.
 */
@Composable
fun makeOverviewSizes(mainViewModel: MainViewModel): OverviewSizes {
    val layout = getLayoutSizes(0, 0, 0)
    return when (layout.sheetHeight) {
        120 -> {
            OverviewSizes(
                80, 30, 120,
                9, 240, 185
            )
        }

        250 -> {
            OverviewSizes(
                90, 42, 135,
                12, 275, 185
            )
        }

        400 -> {
            OverviewSizes(
                150, 80, 150,
                12, 400, 300
            )
        }

        else -> {
            OverviewSizes(
                115, 45, 120,
                10, 240, 185
            )
        }
    }
}
