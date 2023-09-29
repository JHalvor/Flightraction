package com.example.knuseklubben.ui.screens.sigcharts

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.knuseklubben.R
import com.example.knuseklubben.ui.components.menus.getLayoutSizes

@Composable
fun SigchartsScreen(
    navController: NavHostController,
    sigchartsViewModel: SigchartsViewModel
) {
    /**
     * This composable extension function applies conditional padding.
     * @param sheetHeight The height of the sheet used to determine the padding values.
     */
    @Composable
    fun Modifier.applyPadding(sheetHeight: Int) = if (sheetHeight > 230) {
        this.padding(start = 20.dp, end = 20.dp)
    } else {
        this.padding(start = 120.dp, end = 120.dp)
    }

    var sigcharts by remember { mutableStateOf<ImageBitmap?>(null) }
    val layout = getLayoutSizes(customFont = 15, customButton = 0, image = 0)

    val scrollstate = rememberScrollState()
    sigchartsViewModel.sigchart.collectAsState().value.let {
        sigcharts = it
    }

    val backgroundColor = if (sigcharts != null) colorResource(R.color.white)
    else colorResource(R.color.beige_filling)
    Column(
        modifier = Modifier.background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End
        ) {
            Image(
                painter = painterResource(R.drawable.exit_for_menu),
                modifier = Modifier
                    .padding(15.dp)
                    .size(50.dp)
                    .clickable { navController.navigate("MainScreen") }
                    .border(
                        BorderStroke(1.dp, colorResource(R.color.dark_brown_outer_stroke)),
                        shape = CircleShape
                    )
                    .clip(CircleShape),
                contentDescription = stringResource(R.string.exit_button)
            )
        }
        if (sigcharts != null) {
            Text(
                text = stringResource(R.string.info_sigcharts),
                fontSize = layout.customFont.sp,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp, start = 35.dp, end = 35.dp),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollstate),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (sigcharts) {
                null -> {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(30.dp),
                        painter = painterResource(id = R.drawable.question),
                        contentDescription = stringResource(R.string.question_illustration)
                    )
                    Text(
                        text = stringResource(R.string.not_weather_to_show),
                        fontSize = layout.customFont.sp
                    )
                    Text(
                        text = stringResource(R.string.try_again_later),
                        fontSize = layout.customFont.sp
                    )
                }
                else -> {
                    Image(
                        bitmap = sigcharts!!,
                        modifier = Modifier
                            .fillMaxSize()
                            .applyPadding(sheetHeight = layout.sheetHeight),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = stringResource(R.string.sigchart_desc)
                    )
                }
            }
        }
    }
}
