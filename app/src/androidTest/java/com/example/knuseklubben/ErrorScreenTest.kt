package com.example.knuseklubben

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.knuseklubben.ui.screens.ErrorScreen
import org.junit.Rule
import org.junit.Test

class ErrorScreenTest {
    @get:Rule
    val rule = createComposeRule()
    @Test
    fun ErrorScreen_ShowsErrorText() {
        rule.setContent { ErrorScreen() }
        rule.onNodeWithText("Feil under innlasting").assertExists()
    }
}
