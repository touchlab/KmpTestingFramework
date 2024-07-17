package co.touchlab.kmp.testing.framework.compiler.setup

import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import java.nio.file.Path

object PluginConfigurationKeys {

    object IOSTestsGeneratorOutputPath : CompilerConfigurationKey<Path>("iOSTestsGeneratorOutputPath")
    object UnitTestsGeneratorOutputPath : CompilerConfigurationKey<Path>("UnitTestsGeneratorOutputPath")
}

var CompilerConfiguration.iOSTestsGeneratorOutputPath: Path
    get() = getNotNull(PluginConfigurationKeys.IOSTestsGeneratorOutputPath)
    set(value) = put(PluginConfigurationKeys.IOSTestsGeneratorOutputPath, value)

var CompilerConfiguration.unitTestsGeneratorOutputPath: Path
    get() = getNotNull(PluginConfigurationKeys.UnitTestsGeneratorOutputPath)
    set(value) = put(PluginConfigurationKeys.UnitTestsGeneratorOutputPath, value)
