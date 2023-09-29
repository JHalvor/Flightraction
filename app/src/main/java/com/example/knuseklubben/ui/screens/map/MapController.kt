package com.example.knuseklubben.ui.screens.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.focus.FocusManager
import androidx.core.content.ContextCompat
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import com.example.knuseklubben.R
import com.example.knuseklubben.data.model.Airport
import com.example.knuseklubben.data.model.Flight
import com.example.knuseklubben.ui.screens.MainMenus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsDisplay
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private const val INITIAL_ZOOM = 6.45
private const val FOCUS_ZOOM = 10.0
private const val INITIAL_LAT = 63.999
private const val INITIAL_LON = 11.881833

class MapController(
    private val context: Context,
    private val localFocusManager: FocusManager
) {
    private val mutex = Mutex()
    private var markers: MutableList<MarkerData> = mutableListOf()
    private var mapView: MapView? = mapViewFactory(context)

    // Fetching icons for airports and flights from drawable
    val airportIcon =
        ContextCompat.getDrawable(context, R.drawable.airport_marker)
    val flightIcon =
        ContextCompat.getDrawable(context, R.drawable.airplane_plain)

    inner class MarkerData(
        var latDeg: Double,
        var longDeg: Double,
        var title: String,
        var snippet: String,
        var id: String,
        var icon: Drawable?,
        var onClick: () -> Unit
    ) {

        fun updateMarker(
            latDeg: Double? = null,
            longDeg: Double? = null,
            title: String? = null,
            snippet: String? = null,
            id: String? = null,
            icon: Drawable? = null,
        ) {

            latDeg?.let { this.latDeg = it }
            longDeg?.let { this.longDeg = it }
            title?.let { this.title = it }
            snippet?.let { this.snippet = it }
            id?.let { this.id = it }
            icon?.let { this.icon = it }
        }

        /**
         * Adds the markers from the markers list into the current mapview.
         * Checks and validates that the mapview is still valid before adding data
         */
        fun addToMapView(mapViewLocal: MapView) {

            // ERROR PATH
            // Mapview has reloaded, and this thread is still trying to add to the old one
            // due to race conditions. This will create Nullpointers in java, so we disregard
            // this data for now, and it should be rerendered on next round
            if (mapViewLocal != mapView) return

            // SUCCESS PATH
            // Adds the marker to the current mapview
            val marker = Marker(mapView)
            marker.position = GeoPoint(latDeg, longDeg)
            marker.title = title
            marker.snippet = snippet
            marker.id = id
            marker.icon = icon
            marker.setOnMarkerClickListener { _, _ ->
                onClick()
                true
            }
            mapView!!.overlays.add(marker)
        }
    }

    /**
     * Factory that builds the mapView to be ran by an Android view.
     * Creates a default mapView and adds the configuration to run our app
     */
    fun mapViewFactory(context: Context): MapView {
        runBlocking {
            mutex.withLock {
                mapView = MapView(context).apply {
                    //  MAPNIK is for source, aka style on map
                    //  Can change to others that already exist, but unsure how to add new ones
                    setTileSource(TileSourceFactory.MAPNIK)
                    minZoomLevel = 5.0
                    contentDescription = context.getString(R.string.map_content_description)
                    //  userAgentValue must be set because server doesn't accept anonymous requests
                    //  Without it won't load map data
                    val configuration = Configuration.getInstance()
                    configuration.userAgentValue = context.getString(R.string.app_name)

                    //  Starting point and zoom in when map loads for first time
                    controller.setZoom(INITIAL_ZOOM)
                    controller.setCenter(GeoPoint(INITIAL_LAT, INITIAL_LON))

                    val zoomController = this.zoomController
                    val zoomControlDisplay = zoomController.display

                    zoomControlDisplay.setPositions(
                        false,
                        CustomZoomButtonsDisplay.HorizontalPosition.LEFT,
                        CustomZoomButtonsDisplay.VerticalPosition.CENTER,
                    )

                    setMultiTouchControls(true)
                    setOnTouchListener { _, _ ->
                        localFocusManager.clearFocus()
                        performClick()
                        false
                    }
                }
                CoroutineScope(Dispatchers.Default).launch {
                    invalidateAndRedrawMarkers(true)
                }
            }
        }
        // Adds a function that cleans up the mapview on detaching from the android view.
        // is called from the attach lifecycle event to not just invalidate right away.
        mapView?.doOnAttach {
            mapView?.doOnDetach {
                mapView = null
            }
        }
        /*Map view cant be null here*/
        return mapView!!
    }

    /** Function to add the markers that ensures that any new markers added will not generate
     *  Concurancy modification exceptions. This function can be called in a critical section
     *  or it will grab the mutex and enter by itself
     */
    private suspend fun invalidateAndRedrawMarkers(inCritcal: Boolean = false) {
        val clearAndAdd = {
            if (mapView?.overlays != null) {
                mapView!!.overlays!!.clear()
            }
            markers.forEach {
                mapView?.let { it1 -> it.addToMapView(it1) }
            }
            mapView?.invalidate()
        }

        if (inCritcal) {
            clearAndAdd()
        } else {
            mutex.withLock { clearAndAdd() }
        }
    }

    /** For the list of flights supplied by the parameters, check if the flights data is allready
     * in the markers list by comparing callsigns. If it is allready present, update the marker to
     * reflect the new position/rotation of the flight. If the flight is not in the list of markers
     * a new marker is created
     * @param flightData List of flights that we want to add or update in the map
     */
    suspend fun updateFlights(
        flightData: List<Flight>,
        onClickChange: (Any, MainMenus) -> Unit,
    ) {
        mutex.withLock {
            flightData.forEach { flight ->
                if (flight.latitude == null ||
                    flight.longitude == null ||
                    flight.callsign == null
                ) {
                    return
                }
                val rotatedIcon = flight.trueTrack?.let {
                    flightIcon?.let { icon -> rotateIcon(context, icon, it) }
                }
                flight.callsign.let { callsign ->
                    val marker = markers.find { it.id == callsign }

                    if (marker != null) {
                        marker.updateMarker(
                            latDeg = flight.latitude,
                            longDeg = flight.longitude,
                            title = flight.callsign,
                            icon = (rotatedIcon ?: flightIcon),
                        )
                    } else {
                        markers.add(
                            MarkerData(
                                latDeg = flight.latitude!!,
                                longDeg = flight.longitude!!,
                                title = flight.callsign,
                                id = flight.callsign,
                                snippet = "From: ${flight.originCountry}",
                                icon = (rotatedIcon ?: flightIcon),
                                onClick = {
                                    // Opens the flight info menu for the given flight
                                    onClickChange(flight, MainMenus.FLIGHTINFO)
                                }
                            )
                        )
                    }
                }
            }
            // "Invalidating" the map to display the markers as soon as they have been added.
            invalidateAndRedrawMarkers(true)
        }
    }

    /**
     * Rotates a given bitmap to be used as an icon.
     * Using rotational matrix to be applied to the bitmap.
     * @param context for fetching and drawing bitmap resource
     * @param icon Drawable bitmap to be rotated
     * @param rotationAngle degrees from north bitmap icon needs to be rotated
     */
    private fun rotateIcon(context: Context, icon: Drawable, rotationAngle: Double): Drawable {
        // If the icon is not a BitmapDrawable to begin with, it gets converted to a Bitmap
        val bitmapDrawable = (icon as? BitmapDrawable)?.bitmap ?: run {
            val width = icon.intrinsicWidth
            val height = icon.intrinsicHeight
            val config = Bitmap.Config.ARGB_8888
            Bitmap.createBitmap(width, height, config).also { bitmap ->
                val canvas = Canvas(bitmap)
                icon.setBounds(0, 0, canvas.width, canvas.height)
                icon.draw(canvas)
            }
        }
        val matrix = Matrix()
        matrix.postRotate(rotationAngle.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(
            bitmapDrawable,
            0, // The x-coordinate of the first pixel in the source rectangle
            0, // The y-coordinate of the first pixel in the source rectangle
            bitmapDrawable.width,
            bitmapDrawable.height,
            matrix, // The matrix to apply to the bitmap
            true // Whether or not to filter the bitmap
        )
        return BitmapDrawable(context.resources, rotatedBitmap)
    }

    fun resetZoomLevel() {
        mapView?.controller?.setZoom(INITIAL_ZOOM)
        mapView?.controller?.setCenter(GeoPoint(INITIAL_LAT, INITIAL_LON))
    }

    /**
     * Creates markers for airports, creation calls adds to MapUiState.Markers and MapView.overlays.
     * @param airportData the current airports
     * @param onClickChange lambda function added to the onClick event on the markers
     */
    fun initializeAirports(
        airportData: List<Airport>,
        onClickChange: (Any, MainMenus) -> Unit,
    ) {
        airportData.forEach { airport ->
            markers.add(
                MarkerData(
                    airport.latDeg,
                    airport.longDeg,
                    airport.name,
                    "ICAO: ${airport.ident}",
                    airport.id.toString(),
                    airportIcon,
                    onClick = {
                        onClickChange(airport, MainMenus.AIRPORTOVERVIEW)
                    }
                )
            )
        }
        CoroutineScope(Dispatchers.Default).launch {
            invalidateAndRedrawMarkers()
        }
    }

    /**
     * Zooms in on the marker clicked or searched in the searchbar
     * The zoom varies from the diffrent screensizes
     */
    fun zoomOnElement(any: Any, sheetHeight: Int) {
        var lat = 0.0
        var lon = 0.0
        when (any) {
            is Flight -> {
                lat = any.latitude!!
                lon = any.longitude!!
            }
            is Airport -> {
                lat = any.latDeg
                lon = any.longDeg
            }
        }
        mapView?.controller?.setZoom(FOCUS_ZOOM)
        if (300 <sheetHeight) {
            mapView?.controller?.setCenter(GeoPoint(lat - 0.2, lon))
        } else if (sheetHeight < 130) {
            mapView?.controller?.setCenter(GeoPoint(lat - 0.03, lon))
        } else {
            mapView?.controller?.setCenter(GeoPoint(lat - 0.08, lon))
        }
    }
}
