package com.example.knuseklubben.data.network.api

import com.example.knuseklubben.data.model.Attraction
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.opentripmap.com/0.1/"

@OptIn(ExperimentalSerializationApi::class)
private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType())).build()

interface DataSource {
    @GET("en/places/radius")
    suspend fun getAttractions(
        @Query("radius") radius: Int,
        @Query("lon") lon: Double,
        @Query("lat") lat: Double,
        @Query("src_attr") src_attr: String,
        @Query("kinds") kinds: String,
        @Query("rate") rate: Int,
        @Query("format") format: String,
        @Query("limit") limit: Int,
        @Query("apikey") apikey: String
    ): List<Attraction>
}

object PlacesApiRequest {
    val retrofitService: DataSource by lazy {
        retrofit.create(DataSource::class.java)
    }
}
