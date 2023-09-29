package com.example.knuseklubben.data.repository

import com.example.knuseklubben.data.network.api.TafMetarApi
import com.example.knuseklubben.data.network.api.TafMetarApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TafMetarRepository(private val service: TafMetarApiService = TafMetarApi.retrofitService) {
    private suspend fun collectMetarData(icao: String): String {
        return withContext(Dispatchers.IO) {
            service.getMetar(icao)
        }
    }

    /**
     * Doing api-call through collectMetarData, and retrieves last line of textresponse
     */
    suspend fun getLatestMetar(icao: String): String {
        val metarData = collectMetarData(icao)
        val lines = metarData.trimEnd().split("\n")
        return lines.last()
    }
}
