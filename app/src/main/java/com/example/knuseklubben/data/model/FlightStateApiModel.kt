package com.example.knuseklubben.data.model

import kotlinx.serialization.Serializable

// Data model documentation source:
// https://openskynetwork.github.io/opensky-api/rest.html#all-state-vectors
data class FlightStateApiModel(
    val time: Long,
    val states: List<List<Any>>
)

@Serializable
data class FlightState(
    val icao24: String,
    val callsign: String?,
    val originCountry: String?,
    val timePosition: Long?,
    val lastContact: Long?,
    val longitude: Double?,
    val latitude: Double?,
    val baroAltitude: Double?,
    val onGround: Boolean,
    val velocity: Double?,
    val trueTrack: Double?,
    val verticalRate: Double?,
    val sensors: List<Int>?,
    val geoAltitude: Double?,
    val squawk: String?,
    val spi: Boolean?,
    val positionSource: Int?,
    val category: Int?
)
@Serializable
data class FlightStateAircraft(
    val icao24: String,
    val firstSeen: Int?,
    val estDepartureAirport: String?,
    val lastSeen: Int?,
    val estArrivalAirport: String?,
    val callsign: String?,
    val estDepartureAirportHorizDistance: Int?,
    val estDepartureAirportVertDistance: Int?,
    val estArrivalAirportHorizDistance: Int?,
    val estArrivalAirportVertDistance: Int?,
    val departureAirportCandidatesCount: Int?,
    val arrivalAirportCandidatesCount: Int?,
)
