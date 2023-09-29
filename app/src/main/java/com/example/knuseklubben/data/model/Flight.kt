package com.example.knuseklubben.data.model

/**
 * In the following data class, the lines that are commented out have been chosen not to be
 * utilized in our project. However, we have kept them in place since they are part of the API
 * and this allows us the option to utilize them later.
 */
data class Flight(
    val icao24: String?, // Unique ICAO 24-bit address
    val callsign: String?, // Callsign 8 Chars
    val originCountry: String?, // Origin Country
    val timePosition: Long?, // Timestamp of last position update
    val lastContact: Long?, // Timestamp of last update in general
    var longitude: Double?, // WGS-84 long in decimal degrees
    var latitude: Double?, // WGS-84 lat in decimal degrees
    // val baroAltitude: Double?, // Barometric altitude (based on air pressure)
    val onGround: Boolean?, // OnGround indicator
    val velocity: Double?, // Velocity in m/s
    var trueTrack: Double?,
    // Direction in decimal degrees clockwise from north
    // (north=0°)
    // val verticalRate: Double?, // How much it's as/des-cending vertically
    // val sensors: List<Int>?, // IDs of sensors used
    val geoAltitude: Double?, // Geometric altitude in meters
    // val squawk: String?, // Transponder code
    // val spi: Boolean?, // Special purpose indicator
    // val positionSource: Int?, // Origin of state position
    val category: FlightCategory, // See FlightCategory enum class
    var firstSeen: Long?, // Est. time of departure (unix time)
    var departureAirport: String?, // ICAO of est. departure airport
    var lastSeen: Long?, // Est. time of arrival (unix time)
    var arrivalAirport: String?, // ICAO of est. arrival airport
    var flightWeather: WeatherInfo? = null,
) {
    fun isMatchingSearch(searchText: String): Boolean {
        val listOfMatches = listOf(
            "$callsign",
            "${callsign?.take(3)}"
        )
        return listOfMatches.any { it.contains(searchText, ignoreCase = true) }
    }
}

enum class FlightCategory {
    NOINFO,
    NOADSBINFO,
    LIGHT, // < 15500 lbs
    SMALL, // 15500 to 75000 lbs
    LARGE, // 75000 to 300000 lbs
    HIGHVORTEXLARGE, // aircraft such as B-757
    HEAVY, // > 300000 lbs
    HIGHPERFORMANCE, // > 5g acceleration and 400 kts
    ROTORCRAFT,
    GLIDER,
    LIGHTERTHANAIR,
    PARACHUTIST,
    PARAGLIDER,
    RESERVED,
    UNMANNED,
    SPACECRAFT,
    EMERGENCY,
    SERVICE,
    POINTOBSTACLE,
    CLUSTEROBSTACLE,
    LINEOBSTACLE
}
