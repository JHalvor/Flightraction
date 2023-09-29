package com.example.knuseklubben.ui.components.menus

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

data class WindowInfo(
    var screenFinalSize: ScreenSize,
)

enum class ScreenSize {
    XSmall, Small, Medium, Large
}

/**
 * The function returns diffrent Screensize depending on the size and rotation of the screen
 */
@Composable
fun chooseScreenSize(): WindowInfo {
    val configuration = LocalConfiguration.current
    return WindowInfo(
        screenFinalSize = when {
            (
                configuration.screenWidthDp < 490 && configuration.orientation ==
                    Configuration.ORIENTATION_LANDSCAPE
                ) || configuration.screenHeightDp < 350 -> ScreenSize.XSmall

            configuration.screenWidthDp < 600 -> ScreenSize.Small
            configuration.screenWidthDp < 900 && configuration.screenHeightDp < 900 ->
                ScreenSize.Medium

            else -> ScreenSize.Large
        }
    )
}

data class LayoutSizes(
    val sheetHeight: Int,
    val fontSize: Int,
    val bigFont: Int,
    val buttonFont: Int,
    val customFont: Int,
    val customButton: Int,
    val customImage: Int

)

/***
 * Returns a LayoutSizes class to update composables when screen is rotated
 * or if the app is used on a Ipad
 */
@Composable
fun getLayoutSizes(
    customFont: Int,
    customButton: Int,
    image: Int,
): LayoutSizes {
    val window = chooseScreenSize()
    val height: Int
    val font: Int
    val buttonFont: Int
    val bigFont: Int
    val customF: Int
    val customImage: Int
    when (window.screenFinalSize) {
        ScreenSize.XSmall -> {
            height = 120
            font = 10
            buttonFont = 12
            bigFont = 15
            customF = (customFont * 0.6).toInt()
            customImage = image / 2
        }

        ScreenSize.Small -> {
            height = 250
            font = 12
            buttonFont = 22
            bigFont = 30
            customF = customFont
            customImage = image
        }

        ScreenSize.Medium -> {
            height = 140
            font = 11
            buttonFont = 12
            bigFont = 20
            customF = (customFont * 0.8).toInt()
            customImage = image * 2 / 3
        }

        else -> {
            height = 400
            font = 20
            buttonFont = 30
            bigFont = 30
            customF = customFont * 1.5.toInt()
            customImage = image
        }
    }
    return LayoutSizes(
        height, font, bigFont, buttonFont, customF, customButton, customImage
    )
}
