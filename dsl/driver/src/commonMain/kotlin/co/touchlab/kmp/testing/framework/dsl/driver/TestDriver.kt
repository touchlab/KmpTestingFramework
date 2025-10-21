package co.touchlab.kmp.testing.framework.dsl.driver

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface TestDriver {

    val timeout: Duration
        get() = 30.seconds

    fun beforeTest() {
    }

    fun afterTest() {
    }
}
