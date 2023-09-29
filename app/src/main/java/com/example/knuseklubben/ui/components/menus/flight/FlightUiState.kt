package com.example.knuseklubben.ui.components.menus.flight

import com.example.knuseklubben.data.model.Flight

sealed interface FlightUiState {
    data class Success(
        val flightData: List<Flight>,
    ) : FlightUiState

    object Error : FlightUiState
    object Loading : FlightUiState
}
