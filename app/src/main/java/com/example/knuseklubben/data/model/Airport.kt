package com.example.knuseklubben.data.model

import android.util.Log
import com.example.knuseklubben.data.network.api.FlightApi
import com.example.knuseklubben.data.repository.flightRepositoryTAG
import java.io.IOException
import kotlin.random.Random.Default.nextInt
import kotlinx.coroutines.delay
import retrofit2.HttpException

const val MAX_TRIES = 3
const val RETRY_INTERVAL_START_TIME_IN_MS = 1000
const val RETRY_INTERVAL_END_TIME_IN_MS = 2000

// id og ident are unique and never NULL
data class Airport(
    val id: Int, //  Identifier for the airport.
    val ident: String, //  Identifier used in the OutAirports URL. (Gardermoen = ENGM)
    // ICAO, local airport code or an internally-generated
    val type: AirportType, //  Type of airport: "closed_airport", "heliport",
    // "large_airport", "medium_airport", "seaplane_base",
    // and "small_airport"
    val name: String, //  Official airport name
    val latDeg: Double, //  Airport Latitude in decimal degrees (positive for north)
    val longDeg: Double, //  The airport longitude in decimal degrees (positive for east)
    val elevationInFeet: Int, // 	The airport elevation MSL in feet
    val continent: String, //  Code for the continent where the airport is located
    val isoCountry: String, //  Two-character ISO 3166:1-alpha2 code for the country
    // where the airport is located
    val isoRegion: String, //  Alphanumeric code for high-level administrative subdivision
    // of a country where the airport is located
    val municipality: String, //  The primary municipality that the airport serves (kommune)
    val scheduledService: Boolean, // If airport can receive scheduled data
    val gpsCode: String, // Country code
    val iataCode: String, // Identifier for public airport code (Gardermoen = OSL)
    var arrivals: List<FlightArrivalDeparture>? = null, // Arrivals
    var departures: List<FlightArrivalDeparture>? = null, // Departures
    var scheduleData: AirportScheduleData? = null,
    var airportWeather: AirportWeather? = null,
    var airportMetar: String? = null,
    var flights: List<FlightArrivalApiModel>? = null
) {
    /**
     * Checks if user-input matches start of one of the airport's names.
     * Since the user sees airports represented in Norwegian, the function will
     * not return true if the user types "airport".
     */
    fun isMatchingSearch(searchText: String): Boolean {
        val listOfMatches = name.replace("Airport", "Lufthavn").split(" ")
        return listOfMatches.any {
            it.startsWith(searchText, ignoreCase = true)
        }
    }

    /**
     * On success returns the flights arriving to airports.
     * On exception makes a call to the API until MAX_Tries is full.
     *  If there stil are no data the show a error screen.
     */
    suspend fun findIncomingFlight(fligthApi: FlightApi) {

        val currentUnixTime = (System.currentTimeMillis() / 1000L)
        var numTries = 0
        while (numTries < MAX_TRIES) {
            try {
                flights = fligthApi.retrofitService.getFlightByAirportArrivals(
                    ident,
                    currentUnixTime - 86400,
                    currentUnixTime + 86400
                )
                return
            } catch (e: HttpException) {
                when (e.code()) {
                    503 -> { // If to many concurrent requests hits this API it will respond with
                        // 503 error code, so we have to retry if number of concurent requests gets to large
                        numTries++
                        delay(
                            // DELAYS FOR RANDOM INTERVAL TO PREVENT BATCH RETRIES
                            nextInt(
                                RETRY_INTERVAL_START_TIME_IN_MS,
                                RETRY_INTERVAL_END_TIME_IN_MS
                            ).toLong()
                        )
                        continue
                    }
                    else -> {
                        Log.d(
                            flightRepositoryTAG,
                            "no data for $name ${e.message()} ${e.response()}"
                        )
                        return
                    }
                }
            } catch (e: IOException) {
                Log.d(flightRepositoryTAG, "no data for $name")
                return
            }
        }
    }
}

enum class AirportType {
    BALOONPORT,
    BALLOONPORT, // TYPO IN CSV
    CLOSED,
    HELIPORT,
    LARGE_AIRPORT,
    MEDIUM_AIRPORT,
    SEAPLANE_BASE,
    SMALL_AIRPORT
}
