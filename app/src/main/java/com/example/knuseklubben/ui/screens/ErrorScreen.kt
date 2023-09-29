package com.example.knuseklubben.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.knuseklubben.R
import com.example.knuseklubben.ui.components.menus.getLayoutSizes

/**
 * The ErrorScreen composable is used when a error occurs (could be that you don't have wifi)-
 * The screen shows a error message and a warning symbol.
 */
@Composable
fun ErrorScreen() {
    val scrollState = rememberScrollState()
    val layoutSizes = getLayoutSizes(20, 0, 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.beige_filling))
            .verticalScroll(state = scrollState),

    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.error_on_loading),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(25.dp)
                    .padding(top = 16.dp),
                fontSize = layoutSizes.bigFont.sp,
                color = colorResource(R.color.dark_brown_outer_stroke)
            )

            Icon(
                modifier = Modifier.size(size = 120.dp),
                imageVector = Icons.Default.Warning,
                contentDescription = stringResource(R.string.icon_warning),
                tint = colorResource(R.color.dark_brown_outer_stroke)
            )
        }
        Column(horizontalAlignment = Alignment.Start) {
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                modifier = Modifier
                    .padding(start = 50.dp, end = 50.dp),
                text = stringResource(R.string.app_failed_to_load_check_connection),
                fontSize = layoutSizes.customFont.sp,
                color = colorResource(R.color.dark_brown_outer_stroke),
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .padding(top = 10.dp, start = 50.dp, end = 50.dp),
                text = stringResource(R.string.cancel_the_app_and_retry),
                fontSize = layoutSizes.customFont.sp,
                color = colorResource(R.color.dark_brown_outer_stroke),
                textAlign = TextAlign.Center
            )
        }
    }
}
