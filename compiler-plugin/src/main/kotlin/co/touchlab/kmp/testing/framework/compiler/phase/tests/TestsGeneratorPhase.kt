package co.touchlab.kmp.testing.framework.compiler.phase.tests

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteInstanceDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.generator.JUnit4TestsGenerator
import co.touchlab.kmp.testing.framework.compiler.phase.tests.generator.JUnit5TestsGenerator
import co.touchlab.kmp.testing.framework.compiler.phase.tests.generator.XCTestsGenerator
import co.touchlab.kmp.testing.framework.compiler.setup.frameworkConfiguration
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class TestsGeneratorPhase(
    configuration: CompilerConfiguration,
) {

    private val testsSuiteProvider = TestsSuiteProvider(configuration.frameworkConfiguration)

    private val generatorsByKind = listOf(
        JUnit4TestsGenerator,
        JUnit5TestsGenerator,
        XCTestsGenerator,
    ).associateBy { it.kind }

    fun generateTests(irModuleFragment: IrModuleFragment) {
        testsSuiteProvider.findAll(irModuleFragment)
            .forEach {
                generate(it)
            }
    }

    private fun generate(testsSuiteDescriptor: TestsSuiteDescriptor) {
        val testsSuiteInstances = testsSuiteDescriptor.drivers.map { driver ->
            TestsSuiteInstanceDescriptor(
                contracts = testsSuiteDescriptor.contracts,
                driver = driver,
                suiteHasMultipleDrivers = testsSuiteDescriptor.drivers.count { it.testSuiteConfiguration.name == driver.testSuiteConfiguration.name } > 1,
            )
        }

        testsSuiteInstances.forEach { testsSuiteInstanceDescriptor ->
            generatorsByKind[testsSuiteInstanceDescriptor.configuration.kind]?.generate(testsSuiteInstanceDescriptor)
        }
    }
}
