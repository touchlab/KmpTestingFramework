@file:OptIn(ExperimentalForeignApi::class)

package co.touchlab.kmp.testing.framework.dsl.driver

import co.touchlab.kmp.testing.framework.dsl.context.IOSTestContext
import kotlinx.cinterop.ExperimentalForeignApi
import platform.XCTest.XCUIApplication

@IOSDriverType
interface IOSDriver {

    val context: IOSTestContext

    val app: XCUIApplication
        get() = context.app

    fun beforeTest() {
        app.launch()
    }

    fun afterTest() {
    }
}
