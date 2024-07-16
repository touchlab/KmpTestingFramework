package co.touchlab.kmp.testing.framework.compiler.setup

import co.touchlab.kmp.testing.framework.compiler.phase.tests.TestsGeneratorPhase
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class IrExtensionRegistrar(
    private val configuration: CompilerConfiguration,
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        TestsGeneratorPhase(configuration).generateTests(moduleFragment)
    }
}
