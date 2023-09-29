package com.example.knuseklubben.data.model

import kotlinx.serialization.Serializable

data class Place(
    val attactionsWithData: List<AttractionWithData>,
)

data class AttractionWithData(
    val wikidata: String,
    val attraction: Attraction,
    val imageUrl: String,
    val description: String,
)

@Serializable
data class Point(
    val lon: Double,
    val lat: Double
)

@Serializable
data class Attraction(
    val xid: String, // ID for attraction
    val name: String, // Name of attraction
    val dist: Double,
    val rate: Int,
    val wikidata: String,
    val kinds: String, // Search parameters
    val point: Point, // Coordinates
    val osm: String? = null
)
