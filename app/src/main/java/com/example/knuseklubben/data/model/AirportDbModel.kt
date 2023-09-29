package com.example.knuseklubben.data.model

data class AirportDbModel(
    var airports: List<Airport>?,
    var filteredAirports: List<Airport>,
    var scheduledFilteredAirports: List<Airport>
)
