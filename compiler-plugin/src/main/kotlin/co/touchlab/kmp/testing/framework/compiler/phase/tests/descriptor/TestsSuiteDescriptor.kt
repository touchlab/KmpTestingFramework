package co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor

data class TestsSuiteDescriptor(
    val contracts: ContractsDescriptor,
    val androidDrivers: List<DriverDescriptor>,
    val iOSDrivers: List<DriverDescriptor>,
    val unitDrivers: List<DriverDescriptor>,
)
