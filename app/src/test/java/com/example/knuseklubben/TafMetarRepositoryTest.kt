package com.example.knuseklubben

import com.example.knuseklubben.data.network.api.TafMetarApi
import com.example.knuseklubben.data.network.api.TafMetarApiService
import com.example.knuseklubben.data.repository.TafMetarRepository
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class TafMetarRepositoryTest {
    private var apiService: TafMetarApiService = TafMetarApi.retrofitService
    private lateinit var repository: TafMetarRepository

    // Mock data for testing
    private val mockMetarData =
        "ENGM 260620Z 35008KT 320V020 9000 -RASN FEW045 BKN051 00/M03 Q1002 NOSIG=\n" +
            "ENGM 260650Z 36008KT 9999 VCSH FEW049 BKN059 00/M03 Q1002 NOSIG=\n" +
            "ENGM 260720Z 35009KT 320V020 9999 VCSH FEW045 BKN053 01/M04 Q1002 NOSIG=\n" +
            "ENGM 260750Z 36010KT 320V020 9999 VCSH FEW023 OVC061 01/M03 Q1002 NOSIG=\n" +
            "ENGM 260820Z 36009KT 300V030 9999 VCSH FEW025 SCT055 OVC070 01/M04 Q1002 NOSIG=\n" +
            "ENGM 260850Z 36011KT 9999 VCSH FEW024 BKN054 02/M04 Q1002 NOSIG=\n" +
            "ENGM 260920Z 36012KT 320V020 9999 VCSH SCT025 BKN033 02/M04 Q1002 NOSIG=\n"

    // SetUp prepares the environment for each test
    @Before
    fun setUp() {
        // mocks Api-service
        apiService = mock()
        // Creates repository that uses the mocked api-service
        repository = TafMetarRepository(apiService)
    }

    // Tests that the getLatestMetar functions returns the last line, and that it contains
    // data.
    @Test
    fun tafMetarRepository_getLatestMetar_returnsLastLineOfMetardata() = runBlocking {
        val icao = "ENGM"

        // Mocks the return data of the api when its called
        whenever(apiService.getMetar(icao)) doReturn mockMetarData

        // Calls function being tested
        val result = repository.getLatestMetar(icao)

        // Asserts data is received and contains some of the data we are looking for
        assertNotNull(result)
        assertTrue(result.startsWith("ENGM"))
        assertTrue(result.endsWith("="))
    }
}
