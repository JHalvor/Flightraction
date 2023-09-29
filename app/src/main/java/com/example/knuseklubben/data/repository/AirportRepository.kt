package com.example.knuseklubben.data.repository

import android.util.Log
import com.example.knuseklubben.data.db.AirportCSVInput
import com.example.knuseklubben.data.model.*
import com.example.knuseklubben.data.network.api.AirportArrivalDepartureService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

const val scheduleDataTAG = "ScheduleDataError"

class AirportRepository {

    // Loads in the airports at launch.
    private var airportDbModel =
        AirportDbModel(AirportCSVInput.getAirports(), listOf(), listOf())

    lateinit var filteredNorwegianAirports: Flow<List<Airport>>

    init {
        filteredNorwegianAirports = flow {
            emit(getAirports())
        }
    }

    /** Gets the airports from the the database using the applied filter
     * @param filterAirportTypes a list of airport types to filter the search by, defaults to LARGE
     * and MEDIUM sized airports
     */
    fun getAirports(
        filterAirportTypes: List<AirportType> = listOf(
            AirportType.LARGE_AIRPORT,
            AirportType.MEDIUM_AIRPORT
        )
    ): List<Airport> {

        if (airportDbModel.airports.isNullOrEmpty()) {
            airportDbModel.airports = AirportCSVInput.getAirports()
        }
        return airportDbModel.airports!!.filter { airport ->
            // filter out non-Norwegian airports
            airport.isoCountry.startsWith("NO", true) &&
                filterAirportTypes.contains(airport.type)
        }
    }

    /** Function used to find all airports in the repository where the name
     * matches the query string
     */
    suspend fun filterAirports(searchText: String): List<Airport> {
        var returnAirportList = listOf<Airport>()
        filteredNorwegianAirports.collect { airportList ->
            returnAirportList = airportList.filter {
                it.isMatchingSearch(
                    searchText = searchText
                )
            }
        }
        return returnAirportList
    }

    enum class FlightStatus {
        ARRIVAL,
        DEPARTURE,
    }

    /**
     * Gets the filtered list of arrivals or departures from an airport
     */
    private fun getFilteredScheduleDataByFlightStatus(
        status: FlightStatus,
        flightData: AirportScheduleData?
    ): List<FlightArrivalDeparture>? {

        val statusText = when (status) {
            FlightStatus.ARRIVAL -> "A"
            FlightStatus.DEPARTURE -> "D"
        }

        return flightData?.flights?.flightList?.filter {
            it.arrDep.toString() == statusText
        }
    }

    /**
     * Gets the airport data (arrival/departures) for a given airport
     If it has not been called yet, this also calls the AVINOR API
     to collect data
     * @param airport Airport object to get from
     */
    private suspend fun getScheduleAirportData(airport: Airport): AirportScheduleData? {
        if (airport.scheduleData == null) {
            withContext(Dispatchers.IO) {
                try {
                    airport.scheduleData =
                        AirportArrivalDepartureService.retrofitService.getScheduleDataByAirport(
                            airport.iataCode
                        )
                } catch (e: java.lang.RuntimeException) {
                    Log.e(scheduleDataTAG, "No data for airport: ${airport.iataCode}")
                }
            }
        }
        return airport.scheduleData
    }

    /**
     * Fetches airport data from airportRepository, which retrieves it from the airport csv reader
     */
    fun getAirportBasedOnICAOCode(icoaCode: String): Airport? {
        return getAirports().find { it.ident == icoaCode }
    }

    /**
     * Retrieves scheduled airport data for a given airport
     * Updates the arrivals and departures parameters for the airport
     * @param airport the airport that needs scheduled data
     */
    suspend fun fetchSchedulesForAirports(airport: Airport) {
        val flightData = getScheduleAirportData(airport)
        airport.arrivals = getFilteredScheduleDataByFlightStatus(
            FlightStatus.ARRIVAL, flightData
        )
        airport.departures = getFilteredScheduleDataByFlightStatus(
            FlightStatus.DEPARTURE, flightData
        )
    }
}
