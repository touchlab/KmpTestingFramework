package co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor

data class TestsSuiteInstanceDescriptor(
    val contracts: ContractsDescriptor,
    val driver: DriverDescriptor,
    val suiteHasMultipleDrivers: Boolean,
) {

    fun getRequiredImports(fromPackage: String): Set<String> =
        contracts.getRequiredImports(fromPackage) + driver.getRequiredImports(fromPackage)
}
