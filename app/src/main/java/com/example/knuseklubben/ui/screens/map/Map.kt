package com.example.knuseklubben.ui.screens.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.example.knuseklubben.ui.components.menus.LayoutSizes
import com.example.knuseklubben.ui.screens.MainViewModel

/**
 * This composable represents the OSMdroid map.
 * It creates a map view using OSMdroid library and displays it in the UI.
 * @param model The MainViewModel instance containing the necessary data and functions for the map.
 * @param layoutSizes The LayoutSizes instance containing the size information for the map layout.
 */
@Composable
fun Map(
    model: MainViewModel,
    layoutSizes: LayoutSizes
) {
    AndroidView(factory = {
        model.mapController.mapViewFactory(it)
    }) {
        val objectToShow = model.focusedObject.value
        if (objectToShow != null) {
            model.mapController.zoomOnElement(
                objectToShow, layoutSizes.sheetHeight
            )
        }
    }
}
