package com.example.knuseklubben

import com.example.knuseklubben.ui.components.menus.flight.getFormattedDateString
import junit.framework.TestCase.assertEquals
import org.junit.Test

class FlightInfoTest {

    @Test
    fun flightInfo_getFormattedDateString_ReturnsCorrectFormattedDateString() {

        /* Inputdata for testing. Data is purposely varied by including all months and
        * edge-cases for days/hours/min */
        val unixMockData1: Long = 1673078082
        val unixMockData2: Long = 1675206000
        val unixMockData3: Long = 1680299999
        val unixMockData4: Long = 1682415342
        val unixMockData5: Long = 1685007342
        val unixMockData6: Long = 1687685742
        val unixMockData7: Long = 1690277742
        val unixMockData8: Long = 1692956142
        val unixMockData9: Long = 1695634542
        val unixMockData10: Long = 1698226542
        val unixMockData11: Long = 1700908542
        val unixMockData12: Long = 1703500542

        val expectedOutPut1 = "Lørdag 7/1-08:54"
        val result1 = getFormattedDateString(unixMockData1)

        val expectedOutPut2 = "Onsdag 1/2-00:00"
        val result2 = getFormattedDateString(unixMockData2)

        val expectedOutPut3 = "Fredag 31/3-23:59"
        val result3 = getFormattedDateString(unixMockData3)

        val expectedOutPut4 = "Tirsdag 25/4-11:35"
        val result4 = getFormattedDateString(unixMockData4)

        val expectedOutPut5 = "Torsdag 25/5-11:35"
        val result5 = getFormattedDateString(unixMockData5)

        val expectedOutPut6 = "Søndag 25/6-11:35"
        val result6 = getFormattedDateString(unixMockData6)

        val expectedOutPut7 = "Tirsdag 25/7-11:35"
        val result7 = getFormattedDateString(unixMockData7)

        val expectedOutPut8 = "Fredag 25/8-11:35"
        val result8 = getFormattedDateString(unixMockData8)

        val expectedOutPut9 = "Mandag 25/9-11:35"
        val result9 = getFormattedDateString(unixMockData9)

        val expectedOutPut10 = "Onsdag 25/10-11:35"
        val result10 = getFormattedDateString(unixMockData10)

        val expectedOutPut11 = "Lørdag 25/11-11:35"
        val result11 = getFormattedDateString(unixMockData11)

        val expectedOutPut12 = "Mandag 25/12-11:35"
        val result12 = getFormattedDateString(unixMockData12)

        assertEquals(expectedOutPut1, result1)
        assertEquals(expectedOutPut2, result2)
        assertEquals(expectedOutPut3, result3)
        assertEquals(expectedOutPut4, result4)
        assertEquals(expectedOutPut5, result5)
        assertEquals(expectedOutPut6, result6)
        assertEquals(expectedOutPut7, result7)
        assertEquals(expectedOutPut8, result8)
        assertEquals(expectedOutPut9, result9)
        assertEquals(expectedOutPut10, result10)
        assertEquals(expectedOutPut11, result11)
        assertEquals(expectedOutPut12, result12)
    }
}
