package com.example.knuseklubben.data.model

// Data model documentation source:
// https://openskynetwork.github.io/opensky-api/rest.html#arrivals-by-airport

data class FlightArrivalApiModel(
    val icao24: String,
    val firstSeen: Long,
    val estDepartureAirport: String?,
    val lastSeen: Long,
    val estArrivalAirport: String?,
    val callsign: String?,
    val estDepartureAirportHorizDistance: Int?,
    val estDepartureAirportVertDistance: Int?,
    val estArrivalAirportHorizDistance: Int?,
    val estArrivalAirportVertDistance: Int?,
    val departureAirportCandidatesCount: Int?,
    val arrivalAirportCandidatesCount: Int?
)
