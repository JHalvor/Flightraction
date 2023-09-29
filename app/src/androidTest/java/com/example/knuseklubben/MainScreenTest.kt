package com.example.knuseklubben

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.example.knuseklubben.ui.screens.CircleImageButton
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun mainScreen_CircleImageButton_registersClick() {
        val imageName = R.drawable.backarrow_for_menu
        val rotation = mutableStateOf(false)
        val onClick = { rotation.value = !rotation.value }
        val description = "Back Arrow"

        rule.setContent {
            CircleImageButton(
                imageName,
                onClick,
                rotation.value,
                description
            )
        }

        rule.onNodeWithContentDescription("Back Arrow").performClick()
        assert(rotation.value)
    }
}
