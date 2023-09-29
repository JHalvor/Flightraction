package com.example.knuseklubben.data.network.api

import com.example.knuseklubben.BuildConfig
import com.example.knuseklubben.data.network.helpers.okHttpBuilder
import java.util.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL = "https://api.met.no/"

private const val tmApiKey = BuildConfig.API_KEY

val sigchartOkHttpClient: OkHttpClient = okHttpBuilder(tmApiKey)

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(sigchartOkHttpClient)
    .build()

interface SigchartsApiService {
    @GET("weatherapi/sigcharts/2.0/norway")
    suspend fun getWeatherImage(): ResponseBody
}

object SigchartsApiRequest {
    val retrofitService: SigchartsApiService by lazy {
        retrofit.create(SigchartsApiService::class.java)
    }
}
