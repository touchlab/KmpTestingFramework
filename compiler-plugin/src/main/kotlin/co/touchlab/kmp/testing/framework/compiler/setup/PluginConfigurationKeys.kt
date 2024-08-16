package co.touchlab.kmp.testing.framework.compiler.setup

import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import java.nio.file.Path

object PluginConfigurationKeys {

    object AndroidTestsGeneratorOutputPath : CompilerConfigurationKey<Path>("AndroidTestsGeneratorOutputPath")
    object IOSTestsGeneratorOutputPath : CompilerConfigurationKey<Path>("iOSTestsGeneratorOutputPath")
    object UnitTestsGeneratorOutputPath : CompilerConfigurationKey<Path>("UnitTestsGeneratorOutputPath")

    object AndroidAppEntryPoint : CompilerConfigurationKey<String>("AndroidAppEntryPoint")
    object AndroidAppEntryPointType : CompilerConfigurationKey<AndroidInitializationStrategy>("AndroidAppEntryPointType")
}

var CompilerConfiguration.androidTestsGeneratorOutputPath: Path
    get() = getNotNull(PluginConfigurationKeys.AndroidTestsGeneratorOutputPath)
    set(value) = put(PluginConfigurationKeys.AndroidTestsGeneratorOutputPath, value)

var CompilerConfiguration.iOSTestsGeneratorOutputPath: Path
    get() = getNotNull(PluginConfigurationKeys.IOSTestsGeneratorOutputPath)
    set(value) = put(PluginConfigurationKeys.IOSTestsGeneratorOutputPath, value)

var CompilerConfiguration.unitTestsGeneratorOutputPath: Path
    get() = getNotNull(PluginConfigurationKeys.UnitTestsGeneratorOutputPath)
    set(value) = put(PluginConfigurationKeys.UnitTestsGeneratorOutputPath, value)

var CompilerConfiguration.androidAppEntryPoint: String
    get() = getNotNull(PluginConfigurationKeys.AndroidAppEntryPoint)
    set(value) = put(PluginConfigurationKeys.AndroidAppEntryPoint, value)

var CompilerConfiguration.androidAppEntryPointType: AndroidInitializationStrategy
    get() = get(PluginConfigurationKeys.AndroidAppEntryPointType) ?: AndroidInitializationStrategy.COMPOSABLE
    set(value) = put(PluginConfigurationKeys.AndroidAppEntryPointType, value)
