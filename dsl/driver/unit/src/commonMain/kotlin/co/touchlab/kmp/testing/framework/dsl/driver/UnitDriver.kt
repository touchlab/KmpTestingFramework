package co.touchlab.kmp.testing.framework.dsl.driver

import co.touchlab.kmp.testing.framework.dsl.context.UnitTestContext

@UnitDriverType
interface UnitDriver {

    val context: UnitTestContext

    fun beforeTest() {
    }

    fun afterTest() {
    }
}
