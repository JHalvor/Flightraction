package com.example.knuseklubben.data.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "airport")
data class AirportScheduleData(
    @field:Attribute(name = "name")
    var name: String? = null,

    @field:Attribute(name = "noNamespaceSchemaLocation")
    var noNamespaceSchemaLocation: String? = null,

    @field:Element(name = "flights")
    var flights: Flights? = null
)

@Root(name = "flights")
data class Flights(
    @field:Attribute(name = "lastUpdate")
    var lastUpdate: String? = null,

    @field:ElementList(inline = true)
    var flightList: List<FlightArrivalDeparture>? = null
)

@Root(name = "flight")
data class FlightArrivalDeparture(
    @field:Attribute(name = "uniqueID")
    var uniqueID: String? = null,

    @field:Element(name = "via_airport", required = false)
    var via_airport: String? = null,

    @field:Element(name = "airline")
    var airline: String? = null,

    @field:Element(name = "flight_id")
    var flightId: String? = null,

    @field:Element(name = "dom_int", required = false)
    var domInt: String? = null,

    @field:Element(name = "schedule_time", required = false)
    var scheduleTime: String? = null,

    @field:Element(name = "arr_dep", required = false)
    var arrDep: String? = null,

    @field:Element(name = "airport")
    var airport: String? = null,

    @field:Element(name = "check_in", required = false)
    var checkIn: String? = null,

    @field:Element(name = "gate", required = false)
    var gate: String? = null,

    @field:Element(name = "belt", required = false)
    var belt: String? = null,

    @field:Element(name = "status", required = false)
    var status: FlightStatus? = null,

    @field:Element(name = "delayed", required = false)
    var delayed: String? = null
)

@Root(name = "status")
data class FlightStatus(
    @field:Attribute(name = "code")
    var code: String? = null,

    @field:Attribute(name = "time", required = false)
    var time: String? = null
)
