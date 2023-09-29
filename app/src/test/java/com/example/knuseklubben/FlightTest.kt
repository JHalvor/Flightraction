package com.example.knuseklubben

import com.example.knuseklubben.data.model.Flight
import com.example.knuseklubben.data.model.FlightCategory
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FlightTest {
    @Test
    fun flight_isMatchingSearch_returnsListOfMatches() = runBlocking {
        /* Flight objects with data relevant for testing */
        val flight1 = Flight(
            "ABCD",
            "ABC678",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            FlightCategory.LARGE,
            null,
            null,
            null,
            null,
            null
        )
        val flight2 = Flight(
            "DCBA",
            "ABC",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            FlightCategory.LARGE,
            null,
            null,
            null,
            null,
            null
        )
        /* Asserting function being tested returns correct boolean*/
        assertTrue(flight1.isMatchingSearch("ABC"))
        assertFalse(flight1.isMatchingSearch("LOL"))
        assertTrue(flight2.isMatchingSearch("ABC"))
        assertFalse(flight2.isMatchingSearch("LOL"))
    }
}
