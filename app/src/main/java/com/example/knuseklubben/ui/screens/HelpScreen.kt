package com.example.knuseklubben.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.knuseklubben.R
import com.example.knuseklubben.ui.components.menus.LayoutSizes
import com.example.knuseklubben.ui.components.menus.getLayoutSizes

/**
 * This composble makes the helpscreen showing the user what can be done in the app, with examples.
 */
@Composable
fun HelpScreen(navController: NavHostController) {
    val layoutSizes = getLayoutSizes(
        customFont = 20,
        customButton = 0,
        image = 0
    )
    Box {
        Image(
            painter = painterResource(R.drawable.exit_for_menu),
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.TopEnd)
                .zIndex(1f)
                .size(50.dp)
                .border(
                    BorderStroke(1.dp, colorResource(R.color.dark_brown_outer_stroke)),
                    shape = CircleShape
                )
                .clickable { navController.navigate("MainScreen") }
                .clip(CircleShape)
                .background(colorResource(id = R.color.beige_filling)),
            contentDescription = stringResource(R.string.exit_button)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.beige_filling))
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = rememberLazyListState()
        ) {
            item {
                Text(
                    text = stringResource(R.string.header_helpscreen),
                    modifier = Modifier
                        .padding(top = 40.dp, bottom = 16.dp),
                    fontSize = layoutSizes.bigFont.sp,
                    color = colorResource(R.color.dark_brown_outer_stroke)
                )
                Text(
                    text = stringResource(R.string.choose_following),
                    fontSize = layoutSizes.customFont.sp,
                    color = colorResource(R.color.dark_brown_outer_stroke)
                )
                FirstInstructionBox(layoutSizes)

                Text(
                    modifier = Modifier.padding(top = 30.dp),
                    text = stringResource(R.string.thereafter),
                    fontSize = layoutSizes.customFont.sp,
                    color = colorResource(R.color.dark_brown_outer_stroke)
                )
                LastInstructionBox(layoutSizes)
                Spacer(modifier = Modifier.padding(20.dp))
            }
        }
    }
}
/**
 * This is the composable for the first instruction box
 */
@Composable
fun FirstInstructionBox(layoutSizes: LayoutSizes) {
    Column(
        modifier = Modifier
            .padding(start = 50.dp, end = 50.dp)
            .fillMaxWidth()
            .border(
                1.dp,
                colorResource(R.color.dark_brown_outer_stroke),
                shape = RoundedCornerShape(60.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = stringResource(R.string.press_flight),
            fontSize = layoutSizes.fontSize.sp,
            color = colorResource(R.color.dark_brown_outer_stroke),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = stringResource(R.string.press_airport),
            fontSize = layoutSizes.fontSize.sp,
            color = colorResource(R.color.dark_brown_outer_stroke),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = stringResource(R.string.search_flightnumber),
            fontSize = layoutSizes.fontSize.sp,
            color = colorResource(R.color.dark_brown_outer_stroke),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = stringResource(R.string.search_airport),
            fontSize = layoutSizes.fontSize.sp,
            color = colorResource(R.color.dark_brown_outer_stroke),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
    }
}

/**
 * This is the composable for the last instruction box
 */
@Composable
fun LastInstructionBox(layoutSizes: LayoutSizes) {
    Column(
        modifier = Modifier
            .padding(start = 50.dp, end = 50.dp)
            .fillMaxWidth()
            .border(
                1.dp,
                colorResource(R.color.dark_brown_outer_stroke),
                shape = RoundedCornerShape(60.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = stringResource(R.string.click_on),
            fontSize = layoutSizes.fontSize.sp,
            color = colorResource(R.color.dark_brown_outer_stroke),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.icons),
                fontSize = layoutSizes.fontSize.sp,
                color = colorResource(R.color.dark_brown_outer_stroke),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            Image(
                painter = painterResource(R.drawable.button_plane),
                contentDescription = stringResource(R.string.plane_button_example),
                modifier = Modifier.padding(10.dp)
            )
        }
        Text(
            text = stringResource(R.string.or),
            fontSize = layoutSizes.fontSize.sp,
            color = colorResource(R.color.dark_brown_outer_stroke),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.frames),
                fontSize = layoutSizes.fontSize.sp,
                color = colorResource(R.color.dark_brown_outer_stroke),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            Box(
                modifier = Modifier
                    .border(1.dp, colorResource(R.color.dark_brown_outer_stroke))
                    .height(20.dp)
                    .width(100.dp)
                    .padding(10.dp)
            )
        }
    }
}
