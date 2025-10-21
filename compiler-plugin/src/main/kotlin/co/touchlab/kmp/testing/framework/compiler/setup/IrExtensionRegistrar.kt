package co.touchlab.kmp.testing.framework.compiler.setup

import co.touchlab.kmp.testing.framework.compiler.phase.tests.TestsGeneratorPhase
import co.touchlab.kmp.testing.framework.compiler.phase.tests.TestsSuiteProvider
import co.touchlab.kmp.testing.framework.compiler.phase.timeout.AddTimeoutToTestsPhase
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class IrExtensionRegistrar(
    configuration: CompilerConfiguration,
) : IrGenerationExtension {

    private val testsSuiteProvider = TestsSuiteProvider(configuration.frameworkConfiguration)

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val testSuites = testsSuiteProvider.findAll(moduleFragment)

        TestsGeneratorPhase.generateTests(testSuites)

        AddTimeoutToTestsPhase(pluginContext).addTimeout(testSuites)
    }
}
