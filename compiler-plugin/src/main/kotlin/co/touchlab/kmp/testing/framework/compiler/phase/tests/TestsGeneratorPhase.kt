package co.touchlab.kmp.testing.framework.compiler.phase.tests

import co.touchlab.kmp.testing.framework.compiler.phase.tests.generator.AndroidTestsEntryPointGenerator
import co.touchlab.kmp.testing.framework.compiler.phase.tests.generator.IOSTestsEntryPointGenerator
import co.touchlab.kmp.testing.framework.compiler.phase.tests.generator.UnitTestsEntryPointGenerator
import co.touchlab.kmp.testing.framework.compiler.setup.androidAppEntryPoint
import co.touchlab.kmp.testing.framework.compiler.setup.androidAppEntryPointType
import co.touchlab.kmp.testing.framework.compiler.setup.androidContextFactory
import co.touchlab.kmp.testing.framework.compiler.setup.androidTestsGeneratorOutputPath
import co.touchlab.kmp.testing.framework.compiler.setup.iOSContextFactory
import co.touchlab.kmp.testing.framework.compiler.setup.iOSTestsGeneratorOutputPath
import co.touchlab.kmp.testing.framework.compiler.setup.unitContextFactory
import co.touchlab.kmp.testing.framework.compiler.setup.unitTestsGeneratorOutputPath
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class TestsGeneratorPhase(
    configuration: CompilerConfiguration,
) {

    private val testsSuiteProvider = TestsSuiteProvider()

    private val generators = listOf(
        UnitTestsEntryPointGenerator(
            configuration.unitTestsGeneratorOutputPath,
            contextFactoryFunction = configuration.unitContextFactory,
        ),
        AndroidTestsEntryPointGenerator(
            outputDirectory = configuration.androidTestsGeneratorOutputPath,
            androidAppEntryPoint = configuration.androidAppEntryPoint,
            androidInitializationStrategy = configuration.androidAppEntryPointType,
            contextFactoryFunction = configuration.androidContextFactory,
        ),
        IOSTestsEntryPointGenerator(
            outputDirectory = configuration.iOSTestsGeneratorOutputPath,
            contextFactoryFunction = configuration.iOSContextFactory,
        ),
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
