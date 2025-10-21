package co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor

import org.jetbrains.kotlin.ir.declarations.IrClass

data class TestsSuiteDescriptor(
    val contracts: ContractsDescriptor,
    val drivers: List<DriverDescriptor>,
    val testsSuiteClass: IrClass,
)
