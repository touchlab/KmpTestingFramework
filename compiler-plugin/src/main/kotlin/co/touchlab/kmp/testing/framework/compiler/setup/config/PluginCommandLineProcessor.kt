package co.touchlab.kmp.testing.framework.compiler.setup.config

import co.touchlab.kmp.testing.framework.compiler.setup.frameworkConfiguration
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import java.nio.file.Paths
import kotlin.io.path.readText

@OptIn(ExperimentalCompilerApi::class)
class PluginCommandLineProcessor : CommandLineProcessor {

    override val pluginId: String = "co.touchlab.kmp-testing-framework"

    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        Options.configuration,
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        super.processOption(option, value, configuration)

        when (option.optionName) {
            Options.configuration.optionName -> {
                val path = Paths.get(value)

                val fileContent = path.readText()

                configuration.frameworkConfiguration = FrameworkConfiguration.deserialize(fileContent)
            }
        }
    }

    object Options {

        val configuration = CliOption(
            optionName = "configuration",
            valueDescription = "<path>",
            description = "Path to the configuration file.",
            required = true,
            allowMultipleOccurrences = false,
        )
    }
}
