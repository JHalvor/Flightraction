package com.example.knuseklubben.ui.components.menus.place

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.knuseklubben.R
import com.example.knuseklubben.data.model.Airport
import com.example.knuseklubben.data.model.Attraction
import com.example.knuseklubben.data.model.Place
import com.example.knuseklubben.ui.components.menus.getLayoutSizes
import com.example.knuseklubben.ui.screens.MainViewModel
import com.example.knuseklubben.ui.screens.MenuTopBar
import com.example.knuseklubben.ui.screens.ShowErrorTextForApi
import com.example.knuseklubben.ui.screens.ShowLoadingTextForApi
import java.util.Locale

@Composable
fun AttractionMenu(mainViewModel: MainViewModel, place: Place) {
    val placeName = mainViewModel.chosenAirportToShow.collectAsState().value?.name
    val scrollState = rememberScrollState(0)
    val layout = getLayoutSizes(customFont = 0, customButton = 0, image = 0)

    val attractions = place.attactionsWithData

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(layout.sheetHeight.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MenuTopBar(stringResource(R.string.attraction_header), placeName)
        if (attractions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.scrollable(
                    orientation = Orientation.Vertical, state = scrollState
                )
            ) {
                items(attractions.size) { index ->
                    if (attractions[index].attraction.name != "") {
                        val attraction = attractions[index].attraction
                        val imageurl = attractions[index].imageUrl
                        val description = attractions[index].description
                        AttractionCard(
                            attraction, imageurl, description
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .height(layout.sheetHeight.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.no_attractions_to_show),
                    fontSize = 17.sp,
                    color = colorResource(R.color.dark_brown_outer_stroke)
                )
            }
        }
    }
}

/**
 * `AttractionCard` is a Composable that displays an Attraction with its image and description.
 * @param attraction The Attraction object containing the attraction's details.
 * @param imageUrl The URL of the image to be displayed for the attraction.
 * @param description A description of the attraction.
 */
@Composable
private fun AttractionCard(
    attraction: Attraction,
    imageUrl: String,
    description: String,
) {
    // Capitalize-function is depricated, therefore this metod is used.
    val correctDescription = description.replaceFirstChar {
        it.titlecase(Locale.ROOT)
    }
    val layout = getLayoutSizes(customFont = 18, customButton = 0, image = 250)
    val width = if (layout.sheetHeight < 250) {
        60
    } else {
        120
    }
    Row(
        Modifier
            .fillMaxWidth()
            .height(width.dp)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .width(width.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(layout.customImage.dp)
                    .fillMaxSize(),
                contentScale = ContentScale.FillWidth,
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = stringResource(R.string.picture_of) + attraction.name,
            )
        }
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .fillMaxWidth(),
                text = attraction.name,
                fontSize = layout.customFont.sp,
                color = colorResource(R.color.dark_brown_outer_stroke)
            )
            if (correctDescription != "") {
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .fillMaxWidth(),
                    text = correctDescription,
                    fontSize = layout.fontSize.sp,
                    color = colorResource(R.color.dark_brown_outer_stroke)
                )
            }
        }
    }
}

/**
 * Checks if attractions is succesfully retrieved, if so, correct composable is called.
 * If not attractions-data is retrieved, it calls error screen.
 * While loading, loading-composable is called.
 */
@Composable
fun GetPlaceAttractions(
    mainViewModel: MainViewModel,
    airport: Airport
) {
    mainViewModel.getPlaceData(airport = airport)
    when (val placeUiState = mainViewModel.uiStatePlace.collectAsState().value) {
        is PlaceUiState.PlaceSuccess -> AttractionMenu(
            mainViewModel, place = placeUiState.place
        )
        is PlaceUiState.PlaceLoading -> ShowLoadingTextForApi()
        is PlaceUiState.PlaceError -> ShowErrorTextForApi()
    }
}
