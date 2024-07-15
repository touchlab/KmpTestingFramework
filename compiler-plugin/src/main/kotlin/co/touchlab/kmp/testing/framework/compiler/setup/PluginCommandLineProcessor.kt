package co.touchlab.kmp.testing.framework.compiler.setup

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import java.nio.file.Paths

@OptIn(ExperimentalCompilerApi::class)
class PluginCommandLineProcessor : CommandLineProcessor {

    override val pluginId: String = "co.touchlab.kmp.testing.framework"

    override val pluginOptions: Collection<AbstractCliOption> = listOf(
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        super.processOption(option, value, configuration)
    }

    object Options {
    }
}
