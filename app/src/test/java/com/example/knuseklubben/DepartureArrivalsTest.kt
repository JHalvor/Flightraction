package com.example.knuseklubben

import com.example.knuseklubben.ui.components.menus.airport.formatDateString
import junit.framework.TestCase.assertEquals
import org.junit.Test

class DepartureArrivalsTest {

    @Test
    fun departureArrivalsTest_formatDateString_returnsCorrectTimeString() {
        /* input data for testing, including edge cases for hour/min/second */
        val testTime1 = "2023-05-15T12:30:45"
        val testTime2 = "2023-12-31T00:00:00"
        val testTime3 = "2023-01-01T23:59:59"

        assertEquals("12:30", formatDateString(testTime1))
        assertEquals("00:00", formatDateString(testTime2))
        assertEquals("23:59", formatDateString(testTime3))
    }
}
