package co.touchlab.kmp.testing.framework.compiler.phase.tests

import co.touchlab.kmp.testing.framework.compiler.phase.tests.generator.AndroidTestsEntryPointGenerator
import co.touchlab.kmp.testing.framework.compiler.phase.tests.generator.IOSTestsEntryPointGenerator
import co.touchlab.kmp.testing.framework.compiler.phase.tests.generator.UnitTestsEntryPointGenerator
import co.touchlab.kmp.testing.framework.compiler.setup.androidAppEntryPoint
import co.touchlab.kmp.testing.framework.compiler.setup.androidAppEntryPointType
import co.touchlab.kmp.testing.framework.compiler.setup.androidTestsGeneratorOutputPath
import co.touchlab.kmp.testing.framework.compiler.setup.iOSTestsGeneratorOutputPath
import co.touchlab.kmp.testing.framework.compiler.setup.unitTestsGeneratorOutputPath
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class TestsGeneratorPhase(
    configuration: CompilerConfiguration,
) {

    private val testsSuiteProvider = TestsSuiteProvider()

    private val generators = listOf(
        UnitTestsEntryPointGenerator(configuration.unitTestsGeneratorOutputPath),
        AndroidTestsEntryPointGenerator(configuration.androidTestsGeneratorOutputPath, configuration.androidAppEntryPoint, configuration.androidAppEntryPointType),
        IOSTestsEntryPointGenerator(configuration.iOSTestsGeneratorOutputPath),
    )

    fun generateTests(irModuleFragment: IrModuleFragment) {
        val testsSuite = testsSuiteProvider.findAll(irModuleFragment)

        generators.forEach { generator ->
            testsSuite.forEach {
                generator.generate(it)
            }
        }
    }
}
