package com.example.knuseklubben.ui.components.menus.place

import com.example.knuseklubben.data.model.Place

// Interface for retrieving a place that is not in the "pre-cached-list"
sealed interface PlaceUiState {
    data class PlaceSuccess(val place: Place) : PlaceUiState
    object PlaceError : PlaceUiState
    object PlaceLoading : PlaceUiState
}
