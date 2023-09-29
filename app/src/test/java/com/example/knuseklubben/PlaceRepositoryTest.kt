package com.example.knuseklubben

import com.example.knuseklubben.data.model.Airport
import com.example.knuseklubben.data.model.AirportScheduleData
import com.example.knuseklubben.data.model.AirportType
import com.example.knuseklubben.data.model.Attraction
import com.example.knuseklubben.data.model.Description
import com.example.knuseklubben.data.model.Image
import com.example.knuseklubben.data.model.Label
import com.example.knuseklubben.data.model.Page
import com.example.knuseklubben.data.model.Point
import com.example.knuseklubben.data.model.Query
import com.example.knuseklubben.data.model.WikidataDescriptions
import com.example.knuseklubben.data.model.WikidataEntity
import com.example.knuseklubben.data.model.WikidataImages
import com.example.knuseklubben.data.network.api.DataSource
import com.example.knuseklubben.data.network.api.WikidataSourceDescription
import com.example.knuseklubben.data.network.api.WikidataSourceImage
import com.example.knuseklubben.data.repository.PlaceRepository
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class PlaceRepositoryTest {
    private lateinit var apiServicePlace: DataSource
    private lateinit var apiServiceWikiImage: WikidataSourceImage
    private lateinit var apiServiceWikiDesc: WikidataSourceDescription
    private lateinit var repository: PlaceRepository

    private val mockAirportData: Airport = Airport(
        id = 32,
        ident = "",
        type = AirportType.LARGE_AIRPORT,
        name = "Gardermoen",
        latDeg = 60.190300,
        longDeg = 11.065240,
        elevationInFeet = 0,
        continent = "",
        isoCountry = "",
        isoRegion = "",
        municipality = "",
        scheduledService = true,
        gpsCode = "",
        iataCode = "",
        arrivals = emptyList(),
        departures = emptyList(),
        scheduleData = AirportScheduleData(),
        airportWeather = null,
        airportMetar = ""
    )

    private val mockAttractionData: List<Attraction> = listOf(
        Attraction(
            "asdf",
            "Another Museum", 12.0, 5,
            "Q12345", "", Point(60.190300, 60.190300)
        ),
        Attraction(
            "asdf",
            "A Museum", 12.0, 5,
            "Q1234", "", Point(11.065240, 60.190300)
        ),
        Attraction(
            "asdf", "A Museum", 12.0,
            5, "Q1234", "", Point(11.065240, 60.190300)
        )
    )

    private val mockWikiImageData: WikidataImages = WikidataImages(
        Query(
            mapOf(
                "" to Page(
                    title = "Q1234",
                    images = listOf(
                        Image(title = "This is an image.jpg")
                    )
                )
            )
        )
    )

    private val mockWikiDescriptionData: WikidataDescriptions = WikidataDescriptions(
        mapOf(
            "Q1234" to WikidataEntity(
                labels = mapOf("" to Label("no", "a label")),
                descriptions = mapOf("" to Description("en", "a description"))
            )
        )
    )

    @Before
    fun setUp() {
        apiServicePlace = mock()
        apiServiceWikiImage = mock()
        apiServiceWikiDesc = mock()
        repository = PlaceRepository(apiServicePlace, apiServiceWikiImage, apiServiceWikiDesc)
    }

    @Test
    fun placeRepository_getAttractions_returnsPlaceObject() = runBlocking {
        // Mocks the return data of the api when its called
        whenever(
            apiServicePlace.getAttractions(
                any(), // radius
                any(), // latDeg
                any(), // longDeg
                any(), // srcAttr
                any(), // kinds
                any(), // rate
                any(), // format
                any(), // limit
                any(), // apiKey
            )
        ).doReturn(mockAttractionData)
        whenever(apiServiceWikiImage.getImageWikidata(any())).doReturn(mockWikiImageData)
        whenever(apiServiceWikiDesc.getDescriptionWikidata(any())).doReturn(mockWikiDescriptionData)

        val result = repository.getAttractionsByAirport(mockAirportData)

        // Asserting not null of result as well as the objects in the result
        assertNotNull(result)
        assertNotNull(result.attactionsWithData)
        assertNotNull(result.attactionsWithData[0].attraction)
        assertNotNull(result.attactionsWithData[1].imageUrl)
        assertNotNull(result.attactionsWithData[0].description)

        // Asserting that amount attractions is correct and duplicate been removed
        assert(result.attactionsWithData.size == 2)

        // Asserting that image-url is right builded
        assert(result.attactionsWithData[0].imageUrl.endsWith(".jpg"))
        assert(
            result.attactionsWithData[0]
                .imageUrl.startsWith("https://upload.wikimedia.org/wikipedia/commons/")
        )

        // Asserting sorting of attractions by name
        assert(result.attactionsWithData[0].attraction.name == "A Museum")

        // Asserting that mapping to wikidata-id is correct
        assert(result.attactionsWithData[0].wikidata == "Q1234")
        assert(result.attactionsWithData[1].wikidata == "Q12345")

        // Asserting that right combination of attraction,imageUrl and description
        assert(result.attactionsWithData[1].description == "")
        assert(result.attactionsWithData[1].imageUrl == "")
    }
}
