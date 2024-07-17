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
        Options.iOSTestsGeneratorOutputPath,
        Options.unitTestsGeneratorOutputPath,
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        super.processOption(option, value, configuration)

        when (option.optionName) {
            Options.iOSTestsGeneratorOutputPath.optionName -> configuration.iOSTestsGeneratorOutputPath = Paths.get(value)
            Options.unitTestsGeneratorOutputPath.optionName -> configuration.unitTestsGeneratorOutputPath = Paths.get(value)
        }
    }

    object Options {

        val iOSTestsGeneratorOutputPath = CliOption(
            optionName = "iOSTestsGeneratorOutputPath",
            valueDescription = "<path>",
            description = "Path to the output directory for the iOS tests generator.",
            required = true,
            allowMultipleOccurrences = false,
        )

        val unitTestsGeneratorOutputPath = CliOption(
            optionName = "unitTestsGeneratorOutputPath",
            valueDescription = "<path>",
            description = "Path to the output directory for the unit tests generator.",
            required = true,
            allowMultipleOccurrences = false,
        )
    }
}
