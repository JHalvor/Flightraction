package com.example.knuseklubben.data.repository

import com.example.knuseklubben.BuildConfig
import com.example.knuseklubben.data.model.Airport
import com.example.knuseklubben.data.model.Attraction
import com.example.knuseklubben.data.model.AttractionWithData
import com.example.knuseklubben.data.model.Place
import com.example.knuseklubben.data.model.WikidataDescriptions
import com.example.knuseklubben.data.model.WikidataEntity
import com.example.knuseklubben.data.model.WikidataImages
import com.example.knuseklubben.data.network.api.DataSource
import com.example.knuseklubben.data.network.api.WikidataSourceDescription
import com.example.knuseklubben.data.network.api.WikidataSourceImage
import java.security.MessageDigest
import java.util.Locale

class PlaceRepository(
    private val dataSource: DataSource,
    private val wikidataSourceImage: WikidataSourceImage,
    private val wikidataSourceDescription: WikidataSourceDescription
) {
    private val radius = 10000
    private val srcAttr = "wikidata"
    private val kinds =
        "museums,theatres_and_entertainments,view_points," +
            "unclassified_objects,historical_places"
    private val rate = 2
    private val format = "json"
    private val limit = 10
    private val apiKey = BuildConfig.API_KEY_PLACES

    private val baseUrl = "https://upload.wikimedia.org/wikipedia/commons/"

    /** Calls the places API with default values given by the class
     * unless optional parameters are provided.
     */
    private suspend fun getPlacesWithDefaults(
        radius: Int = this.radius,
        lat: Double = 0.0,
        lon: Double = 0.0,
        srcAttr: String = this.srcAttr,
        kinds: String = this.kinds,
        rate: Int = this.rate,
        format: String = this.format,
        limit: Int = this.limit,
    ): List<Attraction> {
        return (
            dataSource.getAttractions(
                radius = radius,
                lat = lat,
                lon = lon,
                src_attr = srcAttr,
                kinds = kinds,
                rate = rate,
                format = format,
                limit = limit,
                apikey = apiKey
            )
            )
    }

    /**
     * Retrieves attractions based on an Airport-object. Based on the varible wikidata
     * from attraction, we get an url from API-call and a wikidataDescription-object. The function
     * stores attraction and a map of this data in a Place-object.
     */
    suspend fun getAttractionsByAirport(airport: Airport): Place {
        val attractions = getPlacesWithDefaults(lat = airport.latDeg, lon = airport.longDeg)

        val uniqueAttractionsSorted = attractions.distinctBy { it.name }.sortedBy { it.name }
        var wikidataTitlesAndIds = ""
        for ((index, item) in uniqueAttractionsSorted.withIndex()) {
            if (index != 0) {
                wikidataTitlesAndIds += "|"
            }
            wikidataTitlesAndIds += item.wikidata
        }

        val wikidataImages = wikidataSourceImage.getImageWikidata(wikidataTitlesAndIds)

        val wikidataDescriptions =
            wikidataSourceDescription.getDescriptionWikidata(wikidataTitlesAndIds)

        return Place(
            crossImageAndDescription(
                uniqueAttractionsSorted, wikidataImages, wikidataDescriptions
            )
        )
    }

    /**
     * Uses a map to connect an attraction to a url and a description.
     * It returns a list with only values, because this connection is stored in separate objects.
     * */
    private fun crossImageAndDescription(
        attractions: List<Attraction>,
        wikidataImages: WikidataImages,
        wikidataDescriptions: WikidataDescriptions
    ): List<AttractionWithData> {
        val map = mutableMapOf<String, AttractionWithData>()

        attractions.forEach {
            map[it.wikidata] = (AttractionWithData(it.wikidata, it, "", ""))
        }

        val wikidataPage = wikidataImages.query?.pages
        wikidataPage?.values?.forEach {
            val imageUrl: String

            if (it.images?.get(0)?.title != null) {
                imageUrl = getUrl(it.images[0].title!!)
                if (it.title != null && imageUrl != "") {
                    // Titles is based on uniqueAttractionsSorted,
                    // and therefor we can just look at title.
                    map[it.title] = map[it.title]!!.copy(imageUrl = imageUrl)
                }
            } else {
                map.remove(it.title)
            }
        }

        map.keys.forEach {
            // Just to get the namevar
            var descripiton = ""

            wikidataDescriptions.entities?.get(it)?.let { it1 ->
                descripiton =
                    map[it]?.attraction?.let { it2 -> findDescription(it2.name, it1) }.toString()
            }

            map[it] = map[it]!!.copy(description = descripiton)
        }
        return map.values.toList()
    }

    private fun getUrl(wikidataImagename: String): String {
        val imageName = wikidataImagename.removePrefix("File:")
            .replace(" ", "_")

        val charsOfHash = md5(imageName)

        // URL needs to have a specific use of its own imageName-hash
        return "$baseUrl${charsOfHash[0]}/$charsOfHash/$imageName"
    }

    /**
     * We want to show a description of a given attraction
     * This function returns the descrpition in the language thats available
     * We only look for descriptions written in English,Bokmål and Nynorsk
     */
    private fun findDescription(
        attractionName: String,
        descriptionObject: WikidataEntity
    ): String {
        var englishDescription = ""

        // Try description:
        descriptionObject.descriptions?.forEach {
            val description = it.value.value?.lowercase(Locale.ENGLISH)
            if (description != null) {
                if (!descSameAsName(attractionName, description)) {
                    when (it.value.language) {
                        "no" -> {
                            return it.value.value!!
                        }

                        "nb" -> {
                            return it.value.value!!
                        }

                        "en" -> {
                            englishDescription = description
                        }
                    }
                }
            }
        }
        // Try label:
        descriptionObject.labels?.forEach {
            val labelDescription = it.value.value?.lowercase(Locale.ENGLISH)
            if (labelDescription != null) {
                if (!descSameAsName(attractionName, labelDescription)) {
                    when (it.value.language) {
                        "no" -> {
                            return it.value.value!!
                        }

                        "nb" -> {
                            return it.value.value!!
                        }

                        "en" -> {
                            if (englishDescription.length < it.value.value!!.length) {
                                englishDescription = labelDescription
                            }
                        }
                    }
                }
            }
        }
        return if (englishDescription != "") englishDescription else ""
    }

    private fun descSameAsName(attractionName: String, description: String): Boolean {
        return attractionName.trim().lowercase(Locale.ENGLISH) == description.trim()
    }

    /**
     * Generates an MD5 hash of an input string, which is needed
     * to get the right URL for wikidata-objects. It creates an instance of the MessageDigest class
     * for the "MD5" algorithm and then computes the MD5 hash by calling the digest method with the
     * input string's bytes. Each hash byte is converted to a hexadecimal string representation.
     * The function returns the first two characters of the resulting string,
     * which represent the first two bytes of the MD5 hash.
     */
    private fun md5(imageName: String): String {
        val messageDigest = MessageDigest.getInstance("MD5")
        val bytes = messageDigest.digest(imageName.toByteArray())
        return bytes.toHex().substring(0, 2)
    }

    /**
     * This extension function converts bytes into hexadecimal representation.
     */
    private fun ByteArray.toHex(): String {
        return joinToString("") { byte -> "%02x".format(byte) }
    }
}
