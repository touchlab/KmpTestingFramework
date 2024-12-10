@file:OptIn(ExperimentalForeignApi::class)

package co.touchlab.kmp.testing.framework.dsl.context

import kotlinx.cinterop.ExperimentalForeignApi
import platform.XCTest.XCUIApplication

interface IOSTestContext {

    val app: XCUIApplication
}
