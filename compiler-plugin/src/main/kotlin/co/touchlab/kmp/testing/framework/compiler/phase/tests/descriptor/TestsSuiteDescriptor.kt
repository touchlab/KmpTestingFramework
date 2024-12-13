package co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor

data class TestsSuiteDescriptor(
    val contracts: ContractsDescriptor,
    val drivers: List<DriverDescriptor>,
)
