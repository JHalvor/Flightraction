package com.example.knuseklubben.data.repository

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.knuseklubben.data.network.api.SigchartsApiRequest
import com.example.knuseklubben.data.network.api.SigchartsApiService
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SigchartsRepository(
    private val sigchartsApi: SigchartsApiService = SigchartsApiRequest.retrofitService
) {
    /**
     * Retrieves the weather image input stream.
     * Makes a network call to the Sigcharts API to get the weather image.
     * Returns the response body as an input stream.
     */
    private suspend fun getSigchartsByteStream(): InputStream {
        return withContext(Dispatchers.IO) {
            val response = sigchartsApi.getWeatherImage()
            response.byteStream()
        }
    }

    /**
     * Retrieves the weather image bitmap.
     */
    suspend fun getSigchartsImageBitmap(): ImageBitmap? {
        val byteStream = getSigchartsByteStream()
        val bitmap = BitmapFactory.decodeStream(byteStream)
        return bitmap?.asImageBitmap()
    }
}
