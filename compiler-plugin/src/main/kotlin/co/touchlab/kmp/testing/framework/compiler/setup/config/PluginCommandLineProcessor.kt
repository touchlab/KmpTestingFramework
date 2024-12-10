package co.touchlab.kmp.testing.framework.compiler.setup.config

import co.touchlab.kmp.testing.framework.compiler.setup.androidAppEntryPoint
import co.touchlab.kmp.testing.framework.compiler.setup.androidAppEntryPointType
import co.touchlab.kmp.testing.framework.compiler.setup.androidContextFactory
import co.touchlab.kmp.testing.framework.compiler.setup.androidTestsGeneratorOutputPath
import co.touchlab.kmp.testing.framework.compiler.setup.iOSContextFactory
import co.touchlab.kmp.testing.framework.compiler.setup.iOSTestsGeneratorOutputPath
import co.touchlab.kmp.testing.framework.compiler.setup.unitContextFactory
import co.touchlab.kmp.testing.framework.compiler.setup.unitTestsGeneratorOutputPath
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
        Options.androidTestsGeneratorOutputPath,
        Options.iOSTestsGeneratorOutputPath,
        Options.unitTestsGeneratorOutputPath,
        Options.androidAppEntryPoint,
        Options.androidContextFactory,
        Options.iOSContextFactory,
        Options.unitContextFactory,
        Options.androidAppEntryPointType,
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        super.processOption(option, value, configuration)

        when (option.optionName) {
            Options.androidTestsGeneratorOutputPath.optionName -> configuration.androidTestsGeneratorOutputPath = Paths.get(value)
            Options.iOSTestsGeneratorOutputPath.optionName -> configuration.iOSTestsGeneratorOutputPath = Paths.get(value)
            Options.unitTestsGeneratorOutputPath.optionName -> configuration.unitTestsGeneratorOutputPath = Paths.get(value)
            Options.androidAppEntryPoint.optionName -> configuration.androidAppEntryPoint = value
            Options.androidAppEntryPointType.optionName -> {
                configuration.androidAppEntryPointType = AndroidInitializationStrategy.byName(value)
            }
            Options.androidContextFactory.optionName -> configuration.androidContextFactory = value
            Options.iOSContextFactory.optionName -> configuration.iOSContextFactory = value
            Options.unitContextFactory.optionName -> configuration.unitContextFactory = value
        }
    }

    object Options {

        val androidTestsGeneratorOutputPath = CliOption(
            optionName = "androidTestsGeneratorOutputPath",
            valueDescription = "<path>",
            description = "Path to the output directory for the Android tests generator.",
            required = true,
            allowMultipleOccurrences = false,
        )

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

        val androidAppEntryPoint = CliOption(
            optionName = "androidAppEntryPoint",
            valueDescription = "function FQ name",
            description = "FQ name of the Composable function which the UI tests should call to setup the app.",
            required = true,
            allowMultipleOccurrences = false,
        )

        val androidAppEntryPointType = CliOption(
            optionName = "androidAppEntryPointType",
            valueDescription = "<Activity|Composable> default: Composable",
            description = "Defines the entrypoint initialization strategy.",
            required = false,
            allowMultipleOccurrences = false,
        )

        val androidContextFactory = CliOption(
            optionName = "androidTestsContextFactory",
            valueDescription = "function FQ name",
            description = "FQ name of a factory function for AndroidTestContext.",
            required = false,
            allowMultipleOccurrences = false,
        )

        val iOSContextFactory = CliOption(
            optionName = "iOSTestsContextFactory",
            valueDescription = "function FQ name",
            description = "FQ name of a factory function for IOSTestContext.",
            required = false,
            allowMultipleOccurrences = false,
        )

        val unitContextFactory = CliOption(
            optionName = "unitTestsContextFactory",
            valueDescription = "function FQ name",
            description = "FQ name of a factory function for UnitTestContext.",
            required = false,
            allowMultipleOccurrences = false,
        )
    }
}
