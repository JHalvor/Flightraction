package com.example.knuseklubben

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.knuseklubben.ui.components.menus.LayoutSizes
import com.example.knuseklubben.ui.screens.LastInstructionBox
import org.junit.Rule
import org.junit.Test

class HelpScreenTest {

    @get:Rule
    val rule = createComposeRule()
    private val layoutsizes = LayoutSizes(
        300,
        10,
        15,
        5,
        3,
        2,
        1
    )

    @Test
    fun checkInstructionBoxElements_ElementsExists() {
        rule.setContent { LastInstructionBox(layoutsizes) }

        rule.onNodeWithText("Klikk på:").assertExists()
        rule.onNodeWithText("Ikoner:").assertExists()
        rule.onNodeWithText("eller").assertExists()
        rule.onNodeWithText("Rammer:").assertExists()
    }
}
