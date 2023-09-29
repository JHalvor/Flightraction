package com.example.knuseklubben.data.network.api

import com.example.knuseklubben.data.model.WikidataDescriptions
import com.example.knuseklubben.data.model.WikidataImages
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://www.wikidata.org/"

private val retrofit =
    Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build()).build()

interface WikidataSourceImage {
    @GET("w/api.php?action=query&prop=images&format=json")
    suspend fun getImageWikidata(
        @Query("titles") titles: String
    ): WikidataImages
}

object WikiApiRequestForImage {
    val retrofitService: WikidataSourceImage by lazy {
        retrofit.create(WikidataSourceImage::class.java)
    }
}

interface WikidataSourceDescription {
    @GET("w/api.php?action=wbgetentities&format=json&props=descriptions|labels")
    suspend fun getDescriptionWikidata(
        @Query("ids") ids: String
    ): WikidataDescriptions
}

object WikiApiRequestForDescription {
    val retrofitService: WikidataSourceDescription by lazy {
        retrofit.create(WikidataSourceDescription::class.java)
    }
}
