package com.example.knuseklubben

import com.example.knuseklubben.data.model.Airport
import com.example.knuseklubben.data.model.AirportType
import com.example.knuseklubben.data.model.Data
import com.example.knuseklubben.data.model.Details
import com.example.knuseklubben.data.model.Instant
import com.example.knuseklubben.data.model.LocationForecastData
import com.example.knuseklubben.data.model.Next1Hours
import com.example.knuseklubben.data.model.Properties
import com.example.knuseklubben.data.model.Summary
import com.example.knuseklubben.data.model.Timeseries
import com.example.knuseklubben.data.network.api.ForecastAPI
import com.example.knuseklubben.data.network.api.ForecastAPIservice
import com.example.knuseklubben.data.repository.LocationForecastRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class LocationForecastRepositoryTest {
    private var apiService: ForecastAPIservice = ForecastAPI.retrofitService
    private lateinit var repository: LocationForecastRepository

    /* Airports used for calling mocked-api*/
    private var mockAirportList: List<Airport> =
        listOf(
            Airport(
                1,
                "ENGM", AirportType.LARGE_AIRPORT, "Gardermoen Lufthavn", 60.0,
                11.0, 200, "Europe",
                "NO", "Oestlandet",
                "Oslo", true, "NOR",
                "OSLOAIRPORT", null,
                null, null, null,
                null
            ),
            Airport(
                2,
                "ENBR", AirportType.LARGE_AIRPORT, "Flesland Lufthavn", 60.0,
                5.0, 200, "Europe", "NO", "Vestlandet",
                "Bergen", true, "NOR",
                "BERGENAIRPORT", null,
                null, null, null,
                null
            )
        )

    /* Weatherdata to be used as mocked api-response */
    val mockWeatherResponse: LocationForecastData =
        LocationForecastData(
            null, null,
            Properties(
                (null),
                arrayListOf<Timeseries>(
                    Timeseries(
                        null,
                        Data(
                            Instant(
                                Details(
                                    null,
                                    10.0,
                                    null,
                                    null,
                                    null,
                                    3.0,
                                    null
                                )
                            ),
                            null,
                            Next1Hours(
                                Summary("clearsky_day"),
                                Details(
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    0.0
                                )
                            ),
                            null
                        )
                    )
                )
            )
        )

    @Before
    fun setUp() {
        /* mocked apiService is injected into repository being tested */
        apiService = mock()
        repository = LocationForecastRepository(apiService)
    }

    @Test
    fun locationForecastRepository_getWeatherForAirport_ReturnsListOfWeatherInfo() =
        runBlocking {
            val airport = mockAirportList[0]
            /* mocking api-response */
            `when`(apiService.getWeather(airport.latDeg, airport.longDeg)).thenReturn(
                mockWeatherResponse
            )
            /* calling function being tested and asserting data is correct */
            val airportWeather = repository.getWeatherForAirport(airport)
            assertEquals("ENGM", airportWeather.airport)
            assertEquals(airportWeather.windSpeed, 3.0)
            assertEquals(airportWeather.temperature, 10.0)
            assertEquals(airportWeather.symbol, "clearsky_day")
        }
}
