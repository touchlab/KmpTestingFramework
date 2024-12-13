@file:OptIn(ExperimentalForeignApi::class)

package co.touchlab.kmp.testing.framework.dsl.driver

import kotlinx.cinterop.ExperimentalForeignApi
import platform.XCTest.XCUIApplication

interface XCUIDriver : TestDriver {

    val context: Context

    val app: XCUIApplication
        get() = context.app

    override fun beforeTest() {
        app.launch()
    }

    override fun afterTest() {
        app.terminate()
    }

    interface Context {

        val app: XCUIApplication
    }
}
