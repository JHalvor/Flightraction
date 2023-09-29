package com.example.knuseklubben.data.network.api

import com.example.knuseklubben.data.model.AirportScheduleData
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://flydata.avinor.no/"

private const val DEFAULT_TO_TIME_IN_HOURS = 10
private const val DEFAULT_FROM_TIME_IN_HOURS = 5

private val retrofit = Retrofit.Builder()
    .addConverterFactory(SimpleXmlConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface AirportArrivalDepartureAPI {
    @GET("XmlFeed.asp")
    suspend fun getScheduleDataByAirport(
        @Query("airport") airport: String,
        @Query("TimeTo") timeTo: Int = DEFAULT_TO_TIME_IN_HOURS,
        @Query("TimeFrom") timeFrom: Int = DEFAULT_FROM_TIME_IN_HOURS
    ): AirportScheduleData
}

object AirportArrivalDepartureService {
    val retrofitService: AirportArrivalDepartureAPI by lazy {
        retrofit.create(AirportArrivalDepartureAPI::class.java)
    }
}
