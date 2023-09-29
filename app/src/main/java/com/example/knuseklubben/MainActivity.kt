package com.example.knuseklubben

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.knuseklubben.data.db.AirportCSVInput
import com.example.knuseklubben.data.repository.*
import com.example.knuseklubben.ui.components.menus.LayoutSizes
import com.example.knuseklubben.ui.components.menus.getLayoutSizes
import com.example.knuseklubben.ui.components.navigation.NavigationControl
import com.example.knuseklubben.ui.screens.MainViewModel
import com.example.knuseklubben.ui.screens.map.MapController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensuring consistent splash screen behavior on pre-33 Android API versions
        installSplashScreen()
        setContent {
            val context = LocalContext.current
            // Sets up a notification system to be used by the entire app.
            // Reads airport CSV to fetch airports
            AirportCSVInput.setContext(context)
            val mapController =
                MapController(context, localFocusManager = LocalFocusManager.current)

            // Sets the size once the app is opened
            val layoutSizes = getLayoutSizes(customFont = 0, customButton = 0, image = 0)

            val mainViewModel: MainViewModel by viewModels(
                factoryProducer = {
                    viewModelFactory(
                        markersRepository = mapController,
                        layoutSizesOnStart = layoutSizes
                    )
                }
            )
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = colorResource(R.color.beige_filling)
            ) {
                NavigationControl(
                    mainViewModel,
                )
            }
        }
    }
}

class viewModelFactory(
    var markersRepository: MapController,
    var layoutSizesOnStart: LayoutSizes
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                mapController = markersRepository,
                layoutSizes = layoutSizesOnStart
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
