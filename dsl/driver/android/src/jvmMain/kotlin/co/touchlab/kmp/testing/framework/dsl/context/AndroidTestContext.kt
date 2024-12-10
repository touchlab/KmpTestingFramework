package co.touchlab.kmp.testing.framework.dsl.context

import androidx.compose.ui.test.junit4.ComposeTestRule

interface AndroidTestContext {

    val composeTestRule: ComposeTestRule

    val activityClass: Class<*>?

    data class Default(
        override val composeTestRule: ComposeTestRule,
        override val activityClass: Class<*>?,
    ) : AndroidTestContext
}
