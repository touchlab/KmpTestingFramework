package co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor

data class TestsSuiteDescriptor(
    val contracts: ContractsDescriptor,
    val iOSDrivers: List<DriverDescriptor>,
)
