package com.example.knuseklubben.data.network.api

import com.example.knuseklubben.BuildConfig
import com.example.knuseklubben.data.network.helpers.okHttpBuilder
import java.util.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.met.no/weatherapi/tafmetar/1.0/"

private const val tmApiKey = BuildConfig.API_KEY

// adds interceptor which automatically adds api-key to all api-calls
// adds user-agent which identifies app to api-provider
val metarOkHttpClient: OkHttpClient = okHttpBuilder(tmApiKey)

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(metarOkHttpClient)
    .addConverterFactory(ScalarsConverterFactory.create())
    // .addConverterFactory(SimpleXmlConverterFactory.create())
    .build()

interface TafMetarApiService {
    @GET("metar.txt")
    suspend fun getMetar(
        @Query("icao") icao: String
    ): String
}

object TafMetarApi {
    val retrofitService: TafMetarApiService by lazy {
        retrofit.create(TafMetarApiService::class.java)
    }
}
