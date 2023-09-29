package com.example.knuseklubben.data.repository

import android.util.Log
import com.example.knuseklubben.data.model.Airport
import com.example.knuseklubben.data.model.Flight
import com.example.knuseklubben.data.model.FlightCategory
import com.example.knuseklubben.data.model.FlightState
import com.example.knuseklubben.data.network.api.FlightApi
import com.example.knuseklubben.ui.components.menus.flight.FlightUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

const val flightRepositoryTAG = "FlightRepository"

class FlightRepository {

    private var flightData = mutableListOf<Flight>()
    private var updatedSet = emptySet<Flight>()

    // For current unix time and hours before current unix time
    private var flightTimeLimit = 10000L

    fun getArrivalDataForAirports(listOfAirports: List<Airport>) {

        listOfAirports.forEach { airport: Airport ->
            CoroutineScope(Dispatchers.IO).launch {
                airport.flights?.forEach { flightResponse ->
                    flightData.filter { flightResponse.icao24 == it.icao24 }.forEach { flight ->
                        flight.firstSeen = flightResponse.firstSeen
                        flight.departureAirport = flightResponse.estDepartureAirport
                        flight.lastSeen = flightResponse.lastSeen
                        flight.arrivalAirport = flightResponse.estArrivalAirport
                        updatedSet = updatedSet.plus(flight)
                        Log.d("Update", flight.toString())
                    }
                }
            }
        }
    }

    /** Called on initializing FlightViewModel, and continously through startFlightUpdateTimer
     *  Updates the flightUiState with flightData every flightTimeLimit
     */
    val latestFlight: Flow<FlightUiState> = flow {
        val updatedData = flightData
        var numTries = 0
        while (true) {
            val flightUiState =
                try {
                    val flightList =
                        mapToListOfStates(
                            FlightApi.retrofitService.getFlightStatesByVector().states
                        )

                    flightList.forEach { flightState ->
                        val flight: Flight? =
                            updatedData.find { it.icao24 == flightState.icao24 }
                        // Flight not seen before, adding new
                        if (flight == null) {
                            flightData.add(
                                Flight(
                                    flightState.icao24,
                                    flightState.callsign,
                                    flightState.originCountry,
                                    flightState.timePosition,
                                    flightState.lastContact,
                                    flightState.longitude,
                                    flightState.latitude,
                                    flightState.onGround,
                                    flightState.velocity,
                                    flightState.trueTrack,
                                    flightState.geoAltitude,
                                    category = FlightCategory.values()[flightState.category ?: 0],
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                )
                            )
                        } else {
                            // Update flight location
                            flight.latitude = flightState.latitude
                            flight.longitude = flightState.longitude
                            flight.trueTrack = flightState.trueTrack
                        }
                    }
                    numTries = 0
                    FlightUiState.Success(
                        updatedData
                    )
                } catch (e: Exception) {
                    if (numTries < 3) {
                        Log.d(flightRepositoryTAG, "ERROR IO $e")
                        numTries++
                        delay(1000)

                        FlightUiState.Success(
                            updatedData
                        )
                        continue
                    }
                    FlightUiState.Error
                }

            emit(flightUiState) // Emits the result of the request to the flow
            delay(flightTimeLimit) // Suspends the coroutine for some time
        }
    }

    /** Map state data from airport arrivals state fligts API call to FlightState object
     * @param stateData list of Any
     * @return list of FlightState objects
     */
    private fun mapToState(stateData: List<Any>): FlightState {
        return FlightState(
            icao24 = (stateData[0] as String).trimStart().trimEnd(),
            callsign = (stateData[1] as? String)?.trimStart()?.trimEnd(),
            originCountry = stateData[2] as? String,
            timePosition = stateData[3] as? Long,
            lastContact = stateData[4] as? Long,
            longitude = stateData[5] as? Double,
            latitude = stateData[6] as? Double,
            baroAltitude = stateData[7] as? Double,
            onGround = stateData[8] as Boolean,
            velocity = stateData[9] as? Double,
            trueTrack = stateData[10] as? Double,
            verticalRate = stateData[11] as? Double,
            sensors = (stateData[12] as? List<*>)?.filterIsInstance<Int>(),
            geoAltitude = stateData[13] as? Double,
            squawk = stateData[14] as? String,
            spi = stateData[15] as? Boolean,
            positionSource = stateData[16] as? Int,
            category = stateData[17] as? Int
        )
    }

    private fun mapToListOfStates(states: List<List<Any>>): List<FlightState> {
        return states.map { mapToState(it) }
    }

    fun filterFlights(textSearch: String): List<Flight> {
        return flightData.filter { it.isMatchingSearch(textSearch) }
    }
}
