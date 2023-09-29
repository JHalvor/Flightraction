package com.example.knuseklubben.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.knuseklubben.data.model.Airport
import com.example.knuseklubben.data.model.Flight
import com.example.knuseklubben.data.network.api.*
import com.example.knuseklubben.data.repository.*
import com.example.knuseklubben.ui.components.menus.LayoutSizes
import com.example.knuseklubben.ui.components.menus.airport.AirportUiState
import com.example.knuseklubben.ui.components.menus.flight.FlightUiState
import com.example.knuseklubben.ui.components.menus.place.PlaceUiState
import com.example.knuseklubben.ui.components.searchbar.SearchUiState
import com.example.knuseklubben.ui.screens.map.MapController
import java.io.IOException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.HttpException

class MainViewModel(
    private val placeRepository: PlaceRepository = PlaceRepository(
        PlacesApiRequest.retrofitService,
        WikiApiRequestForImage.retrofitService,
        WikiApiRequestForDescription.retrofitService,
    ),
    layoutSizes: LayoutSizes,
    private val airportRepository: AirportRepository =
        AirportRepository(),
    private val metarRepository: TafMetarRepository =
        TafMetarRepository(TafMetarApi.retrofitService),
    private val locationForecastRepository: LocationForecastRepository =
        LocationForecastRepository(),
    private val flightRepository: FlightRepository = FlightRepository(),
    var mapController: MapController,
) : ViewModel() {
    // STATES
    private val _uiStatePlace: MutableStateFlow<PlaceUiState> =
        MutableStateFlow(PlaceUiState.PlaceLoading)
    val uiStatePlace: StateFlow<PlaceUiState> = _uiStatePlace.asStateFlow()

    private val _airportUiState = MutableStateFlow(AirportUiState(airports = listOf()))
    val airportUiState = _airportUiState.asStateFlow()

    // FLight UI States
    private var _flightUiState: MutableStateFlow<FlightUiState> =
        MutableStateFlow(FlightUiState.Loading)
    val flightUiState: StateFlow<FlightUiState> = _flightUiState.asStateFlow()
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    // MAP UI States

    private val _focusedObject = MutableStateFlow<Any?>(null)
    val focusedObject = _focusedObject.asStateFlow()

    // MENU AND NAVIGATION STATES
    private val _stackWithEarlierObjects = mutableStateOf(listOf<Pair<Any?, MainMenus>>())
    val stackWithEarlierObjects: State<List<Pair<Any?, MainMenus>>> = _stackWithEarlierObjects

    private var _sheetState = MutableStateFlow(false)
    var sheetState = _sheetState.asStateFlow()

    private var _airportToShow = MutableStateFlow<Airport?>(null)
    private var _fligthToShow = MutableStateFlow<Flight?>(null)

    var chosenFlightToShow = _fligthToShow.asStateFlow()
    var chosenAirportToShow = _airportToShow.asStateFlow()

    private var _whatMenuToShow = MutableStateFlow(MainMenus.NOTHING)
    var whatMenuToShow: StateFlow<MainMenus> = _whatMenuToShow

    // Searchbar states
    private val _searchUiState = MutableStateFlow(
        SearchUiState(
            false, "", emptyList(), emptyList()
        )
    )
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    private val onClickChange = { any: Any, menu: MainMenus ->
        resetState()
        if (navigateForwardToElement(any, menu, layoutSizes) || !_sheetState.value) {
            toggleSheetState()
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            airportRepository.filteredNorwegianAirports.collect { airports ->
                _airportUiState.update { currentState ->
                    currentState.copy(
                        airports = airports
                    )
                }
                mapController.initializeAirports(
                    airports,
                    onClickChange,
                )
                launch {
                    airports.forEach {
                        it.findIncomingFlight(FlightApi)
                    }
                }
            }

            flightRepository.latestFlight.collect { flights ->
                _flightUiState.value = flights
                when (flights) {
                    is FlightUiState.Success -> {
                        mapController.updateFlights(
                            flights.flightData,
                            onClickChange,
                        )
                        flightRepository.getArrivalDataForAirports(_airportUiState.value.airports)
                    }

                    else -> {}
                }
            }
        }
    }
    /**
     * Retrieves the correct weather to show in airportmenu.
     */
    fun onClickedAirport(airport: Airport) {
        _focusedObject.value = airport
        viewModelScope.launch {
            airport.airportWeather = locationForecastRepository.getWeatherForAirport(airport)
            airport.airportMetar = metarRepository.getLatestMetar(airport.ident)
            airportRepository.fetchSchedulesForAirports(airport)
        }
    }

    /**
     * Retrieves the correct weather to show in flightmenu if coordinates are not null.
     */
    fun onClickedFlight(flight: Flight) {
        _focusedObject.value = flight
        viewModelScope.launch {
            if (flight.latitude != null && flight.longitude != null) {
                flight.flightWeather =
                    locationForecastRepository.getWeather(flight.latitude!!, flight.longitude!!)
            }
        }
    }

    fun resetZoomOnMap() {
        mapController.resetZoomLevel()
    }
    fun resetFocusObject() {
        _focusedObject.value = null
    }

    /**
     * Checks if API-call for getting places/attractions is succsessful. If it is, the function
     * calls the correct composable for showing attractions. If not, an user-friendly error
     * will occur.
     */
    fun getPlaceData(airport: Airport) {
        viewModelScope.launch {
            _uiStatePlace.update {
                try {
                    PlaceUiState.PlaceSuccess(
                        placeRepository.getAttractionsByAirport(airport = airport)
                    )
                } catch (e: IOException) {
                    PlaceUiState.PlaceError
                } catch (e: HttpException) {
                    PlaceUiState.PlaceError
                }
            }
        }
    }

    /**
     * cancels timing-scope when viewmodel's lifecycle ends
     */
    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    fun getAirportBasedOnICAOCode(it: String): Airport? {
        return airportRepository.getAirportBasedOnICAOCode(it)
    }

    fun updateMapFocus(chosenObject: Any) {
        _focusedObject.value = chosenObject
    }

    /**
     * Updates and sets the icons under the searchbar if true, hides icons if argument is false
     */
    fun updateHideIconsTo(boolean: Boolean) {
        _searchUiState.update { currentState ->
            currentState.copy(
                hideIcons = boolean
            )
        }
    }

    /**
     * Updates the searchbar-field to show chosen flight from clicking on a marker
     * or on a autosuggest.
     */
    fun onClickedFlightUpdateSearchbarText(flight: Flight) {
        val callsign = flight.callsign ?: "Ukjent"
        val departure = flight.departureAirport ?: "Ukjent"
        val arrival = flight.arrivalAirport ?: "Ukjent"
        onTextSearchChange("$callsign: $departure - $arrival")
    }

    /**
     * Retrieves the filtered flightes and airports who are matching criterias for the
     * text from search.
     * @param text Text som searchfield
     */
    fun onTextSearchChange(text: String) {
        viewModelScope.launch {
            _searchUiState.update { currentState ->
                currentState.copy(
                    textSearch = text,
                    filteredFlights = flightRepository.filterFlights(textSearch = text),
                    filteredAirports = airportRepository.filterAirports(text)
                )
            }
        }
    }

    /**
     * This function clears the search field of text when the user either clicks on the cross
     * icon in the search field or in the menu.
     */
    fun emptyText() {
        _searchUiState.update { currentState ->
            currentState.copy(
                textSearch = ""
            )
        }
    }

    /** Push any focus element to a stack that can be retraced if we are navigating backwards
     */
    private fun pushNewObjectToStack(lastObject: Any?) {
        val currentStack = _stackWithEarlierObjects.value
        val newStack = currentStack.toMutableList()
        newStack.add(Pair(lastObject, _whatMenuToShow.value))
        _stackWithEarlierObjects.value = newStack.toList()
    }

    /** Returns the most recently useded element and the menu assosiated with that element */
    private fun popObjectFromStack(): Pair<Any?, MainMenus>? {
        val currentStack = _stackWithEarlierObjects.value
        if (currentStack.isEmpty()) {
            return null
        }
        val newStack = currentStack.toMutableList()
        val lastObject = newStack.removeAt(newStack.lastIndex)
        _stackWithEarlierObjects.value = newStack.toList()
        return lastObject
    }

    /** Call for UI elements to navigate backwards in their history.
     *  Will put any relevant element back into focus and restore the most recently used menu
     *  */
    fun navigateBackward() {
        popObjectFromStack()?.let { pair ->
            when (pair.first) {
                is Airport -> {
                    updateCurrentAirport(pair.first as Airport)
                }
                is Flight -> {
                    updateCurrentFlight(pair.first as Flight)
                }
            }
            _whatMenuToShow.value = pair.second
        }
    }

    /** Navigates to the given menu without putting any elements into focus,
     * and keeps the current menu in the history stack
     * @param mainMenus Menu to go to next
     *  */
    fun navigateForwardToMenu(mainMenus: MainMenus) {
        pushNewObjectToStack(null)
        _whatMenuToShow.value = mainMenus
    }

    /**
     * In the Flightinfo menu you have the option to change the information shown in the menu.
     * This function changes the general info to the weather info.
     */
    fun toggleFligthWeatherDisplay() {
        _whatMenuToShow.value = when (_whatMenuToShow.value) {
            MainMenus.FLIGHTWEATHER -> {
                MainMenus.FLIGHTINFO
            }
            MainMenus.FLIGHTINFO -> {
                MainMenus.FLIGHTWEATHER
            }
            else -> {
                _whatMenuToShow.value
            }
        }
    }

    /**
     * Resets the state of chosen objects, and sets the menu to be nothing since the object is not
     * chosen. Empties the stack to avoid the user to navigate unnecessary backwards.
     */
    fun resetState() {
        _sheetState.value = false
        _fligthToShow.value = null
        _airportToShow.value = null
        _whatMenuToShow.value = MainMenus.NOTHING
        _stackWithEarlierObjects.value = listOf()
    }

    /**
     * Pushes the chosen object to the stack who keeps track of which element that were navigated
     * to/from. Updates to show the correct menu.
     * @param element The object that user is navigating to
     * @param mainMenus The menu to show
     */
    fun navigateForwardToElement(
        element: Any,
        mainMenus: MainMenus,
        layoutSizes: LayoutSizes
    ): Boolean {
        when (element) {
            is Airport -> {
                onTextSearchChange(element.name)
                updateCurrentAirport(element)
            }

            is Flight -> {
                onClickedFlightUpdateSearchbarText(element)
                updateCurrentFlight(element)
            }
        }
        mapController.zoomOnElement(element, layoutSizes.sheetHeight)
        pushNewObjectToStack(element)
        _whatMenuToShow.value = mainMenus
        return false
    }

    /** Updates airport as focus element and adds it to the history stack*/
    private fun updateCurrentAirport(airport: Airport) {
        _airportToShow.value = airport
    }

    /** Updates flight as focus element and adds it to the history stack*/
    private fun updateCurrentFlight(flight: Flight) {
        _fligthToShow.value = flight
    }

    /** Toggles the bottom sheet based on parameter*/
    fun toggleSheetState(boolean: Boolean? = null) {
        if (boolean == null) {
            _sheetState.value = !_sheetState.value
            return
        }
        _sheetState.value = boolean
    }
}
