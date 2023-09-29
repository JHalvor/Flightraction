package com.example.knuseklubben.data.network.api

import com.example.knuseklubben.BuildConfig
import com.example.knuseklubben.data.model.FlightArrivalApiModel
import com.example.knuseklubben.data.model.FlightStateApiModel
import okhttp3.Credentials
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

private const val BASE_URL = "https://opensky-network.org/api/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface FlightApiService {

    @GET("states/all?extended=1")
    suspend fun getFlightStatesByVector(
        @Query("lamin") lamin: Double = 58.078,
        @Query("lomin") lomin: Double = 4.992,
        @Query("lamax") lamax: Double = 80.657,
        @Query("lomax") lomax: Double = 31.291,

        @Header("Authorization") authHeader: String = Credentials.basic(
            BuildConfig.OPENSKY_USERNAME,
            BuildConfig.OPENSKY_PASSWORD
        )
    ): FlightStateApiModel

    @GET("flights/arrival?")
    suspend fun getFlightByAirportArrivals(
        @Query("airport") ident: String, // ICAO
        @Query("begin") beginUnixTime: Long,
        @Query("end") endUnixTime: Long,
        @Header("Authorization") authHeader: String = Credentials.basic(
            BuildConfig.OPENSKY_USERNAME,
            BuildConfig.OPENSKY_PASSWORD
        )
    ): List<FlightArrivalApiModel>
}

object FlightApi {
    val retrofitService: FlightApiService by lazy {
        retrofit.create(FlightApiService::class.java)
    }
}
