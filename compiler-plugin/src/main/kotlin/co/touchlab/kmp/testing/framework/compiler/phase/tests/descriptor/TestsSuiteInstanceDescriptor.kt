package co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor

import co.touchlab.kmp.testing.framework.compiler.setup.config.TestSuiteConfiguration

data class TestsSuiteInstanceDescriptor(
    val contracts: ContractsDescriptor,
    val driver: DriverDescriptor,
    val suiteHasMultipleDrivers: Boolean,
) {

    val configuration: TestSuiteConfiguration
        get() = driver.testSuiteConfiguration

    fun getRequiredImports(fromPackage: String): Set<String> =
        contracts.getRequiredImports(fromPackage) + driver.getRequiredImports(fromPackage)
}
