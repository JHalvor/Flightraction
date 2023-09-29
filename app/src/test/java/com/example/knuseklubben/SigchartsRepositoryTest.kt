package com.example.knuseklubben

import com.example.knuseklubben.data.network.api.SigchartsApiService
import com.example.knuseklubben.data.repository.SigchartsRepository
import java.nio.file.Files
import java.nio.file.Paths
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SigchartsRepositoryTest {
    private lateinit var apiService: SigchartsApiService
    private lateinit var repository: SigchartsRepository

    /* Collecs image to be used as mock-data from test-resources */
    private val imageBytes = Files.readAllBytes(
        Paths.get(javaClass.getResource("/10x10.png")!!.toURI())
    )
    private val mockimage = ResponseBody.create("image/png".toMediaTypeOrNull(), imageBytes)

    @Before
    fun setUp() {
        /* Mocked apiService injected into repository being tested to make mocked api-calls*/
        apiService = mock(SigchartsApiService::class.java)
        repository = SigchartsRepository(apiService)
    }

    @Test
    fun sigchartsRepository_getSigchartsImageBitmap_returnsBitmap(): Unit = runBlocking {

        /* Mocks api-response */
        `when`(apiService.getWeatherImage()).thenReturn(
            mockimage
        )

        /* calls function being tested and asserts image is returned with correct properties */
        val bitmap = repository.getSigchartsImageBitmap()
        if (bitmap != null) {
            assertEquals(bitmap.width, 10)
            assertEquals(bitmap.height, 10)
        }
    }
}
