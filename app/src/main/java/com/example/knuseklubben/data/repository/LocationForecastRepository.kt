package com.example.knuseklubben.data.repository

import com.example.knuseklubben.data.model.Airport
import com.example.knuseklubben.data.model.AirportWeather
import com.example.knuseklubben.data.model.LocationForecastData
import com.example.knuseklubben.data.model.WeatherInfo
import com.example.knuseklubben.data.network.api.ForecastAPI
import com.example.knuseklubben.data.network.api.ForecastAPIservice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationForecastRepository(
    private val service: ForecastAPIservice = ForecastAPI.retrofitService
) {
    private suspend fun collectWeatherData(lat: Double, lon: Double): LocationForecastData {
        return withContext(Dispatchers.IO) {
            service.getWeather(lat, lon)
        }
    }

    // gets weather information by coordinates
    suspend fun getWeather(lat: Double, lon: Double): WeatherInfo {
        val weather = collectWeatherData(lat, lon)

        // Parsing properties
        val data = weather.properties?.timeseries?.get(0)?.data
        val instant = data?.instant?.details

        return WeatherInfo(
            temperature = instant?.airTemperature,
            windSpeed = instant?.windSpeed,
            precipitation = data?.next1Hours?.details?.precipitationAmount,
            symbol = data?.next1Hours?.summary?.symbolCode
        )
    }

    /** gets weather and meta information for airports. airportparameter is icao-code
     */
    suspend fun getWeatherForAirport(airport: Airport): AirportWeather {

        val weather = collectWeatherData(airport.latDeg, airport.longDeg)

        // Parsing properties
        val data = weather.properties?.timeseries?.get(0)?.data
        val instant = data?.instant?.details

        return AirportWeather(
            airport = airport.ident,
            temperature = instant?.airTemperature,
            windSpeed = instant?.windSpeed,
            precipitation = data?.next1Hours?.details?.precipitationAmount,
            symbol = data?.next1Hours?.summary?.symbolCode
        )
    }
}
