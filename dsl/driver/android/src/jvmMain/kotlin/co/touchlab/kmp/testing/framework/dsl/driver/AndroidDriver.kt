package co.touchlab.kmp.testing.framework.dsl.driver

import androidx.compose.ui.test.junit4.ComposeTestRule
import co.touchlab.kmp.testing.framework.dsl.context.AndroidTestContext

@AndroidDriverType
interface AndroidDriver {

    val context: AndroidTestContext

    val composeTestRule: ComposeTestRule
        get() = context.composeTestRule

    val activityClass: Class<*>?
        get() = context.activityClass

    fun beforeTest() {
    }

    fun afterTest() {
    }
}
