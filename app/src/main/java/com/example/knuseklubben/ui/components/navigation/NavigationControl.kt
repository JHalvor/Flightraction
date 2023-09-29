package com.example.knuseklubben.ui.components.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.knuseklubben.R
import com.example.knuseklubben.ui.components.menus.flight.FlightUiState
import com.example.knuseklubben.ui.components.searchbar.SearchBar
import com.example.knuseklubben.ui.screens.ErrorScreen
import com.example.knuseklubben.ui.screens.HelpScreen
import com.example.knuseklubben.ui.screens.MainMenus
import com.example.knuseklubben.ui.screens.MainScreen
import com.example.knuseklubben.ui.screens.MainViewModel
import com.example.knuseklubben.ui.screens.sigcharts.SigchartsScreen
import com.example.knuseklubben.ui.screens.sigcharts.SigchartsViewModel

/**
 * Helps navigate betweeen diffrent screens in the app.
 */
@Composable
fun NavigationControl(
    mainViewModel: MainViewModel,
) {
    val hideIcons by remember { mutableStateOf(false) }

    val navController: NavHostController = rememberNavController()
    val sigchartsViewModel = SigchartsViewModel()

    NavHost(navController = navController, startDestination = "MainScreen") {
        composable("MainScreen") {
            MainScreen(
                mainViewModel,
            )

            Column {
                SearchBar(mainViewModel)
                if (!hideIcons) {
                    ShowIcons(mainViewModel, navController, sigchartsViewModel)
                }
            }
        }

        composable("HelpScreen") {
            HelpScreen(navController = navController)
        }

        composable("ErrorScreen") {
            ErrorScreen()
        }
        composable("SigchartDisplay") {
            SigchartsScreen(navController = navController, sigchartsViewModel = sigchartsViewModel)
        }
    }
    val flightUi = mainViewModel.flightUiState.collectAsState().value
    if (flightUi is FlightUiState.Error) {
        navController.navigate("ErrorScreen")
    }
}

/**
 * This functions makes the icons to press to get to the two screens SigchartsScreen and HelpScreen
 */
@Composable
fun ShowIcons(
    mainViewModel: MainViewModel,
    navController: NavHostController,
    sigchartsViewModel: SigchartsViewModel,
) {
    val menu by mainViewModel.whatMenuToShow.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.End
    ) {
        RoundedIconDisplay(
            navFunction = { navController.navigate("HelpScreen") },
            drawableID = R.drawable.button_help,
            contentDescription = stringResource(R.string.help_button)
        )

        RoundedIconDisplay(
            navFunction = {
                navController.navigate("SigchartDisplay")
                sigchartsViewModel.onClickedSigchartButton()
            },
            drawableID = R.drawable.button_sigcharts,
            contentDescription = stringResource(R.string.sigchart_button)
        )
        when (menu) {
            MainMenus.FLIGHTWEATHER -> {
                RoundedIconDisplay(
                    navFunction = {
                        mainViewModel.toggleFligthWeatherDisplay()
                        mainViewModel.toggleSheetState(true)
                    },
                    drawableID = R.drawable.button_plane,
                    contentDescription = stringResource(R.string.plane_button)
                )
            }
            MainMenus.FLIGHTINFO -> {
                RoundedIconDisplay(
                    navFunction = {
                        mainViewModel.toggleFligthWeatherDisplay()
                        mainViewModel.toggleSheetState(true)
                    },
                    drawableID = R.drawable.button_weather,
                    contentDescription = stringResource(R.string.weather_for_fligth_button)
                )
            }
            else -> {}
        }
    }
}

/**
 * Function used to format the icons shown on the app
 */
@Composable
fun RoundedIconDisplay(navFunction: () -> Unit, drawableID: Int, contentDescription: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 15.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .clickable(
                    onClick = {
                        navFunction()
                    }
                )
        ) {
            Image(
                painter = painterResource(id = drawableID),
                contentDescription = contentDescription
            )
        }
    }
}
