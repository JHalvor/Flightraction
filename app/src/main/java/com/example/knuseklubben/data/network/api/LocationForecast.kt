package com.example.knuseklubben.data.network.api

import com.example.knuseklubben.BuildConfig
import com.example.knuseklubben.data.model.LocationForecastData
import com.example.knuseklubben.data.network.helpers.okHttpBuilder
import java.util.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.met.no/weatherapi/locationforecast/2.0/"

// Reads api-key from local.properties

private const val lfApiKey = BuildConfig.API_KEY

val okHttpClient = okHttpBuilder(lfApiKey)

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface ForecastAPIservice {
    @GET("compact")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ):
        LocationForecastData
}

object ForecastAPI {
    val retrofitService: ForecastAPIservice by lazy {
        retrofit.create(ForecastAPIservice::class.java)
    }
}
