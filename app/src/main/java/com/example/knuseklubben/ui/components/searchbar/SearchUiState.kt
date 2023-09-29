package com.example.knuseklubben.ui.components.searchbar

import com.example.knuseklubben.data.model.Airport
import com.example.knuseklubben.data.model.Flight

data class SearchUiState(
    val hideIcons: Boolean,
    val textSearch: String,
    val filteredFlights: List<Flight>,
    val filteredAirports: List<Airport>
)
