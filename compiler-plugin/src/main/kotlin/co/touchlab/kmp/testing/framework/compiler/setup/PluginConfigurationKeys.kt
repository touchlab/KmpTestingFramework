package co.touchlab.kmp.testing.framework.compiler.setup

import co.touchlab.kmp.testing.framework.compiler.setup.config.AndroidInitializationStrategy
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import java.nio.file.Path

object PluginConfigurationKeys {

    object AndroidTestsGeneratorOutputPath : CompilerConfigurationKey<Path>("AndroidTestsGeneratorOutputPath")
    object IOSTestsGeneratorOutputPath : CompilerConfigurationKey<Path>("iOSTestsGeneratorOutputPath")
    object UnitTestsGeneratorOutputPath : CompilerConfigurationKey<Path>("UnitTestsGeneratorOutputPath")

    object AndroidAppEntryPoint : CompilerConfigurationKey<String>("AndroidAppEntryPoint")
    object AndroidAppEntryPointType : CompilerConfigurationKey<AndroidInitializationStrategy>("AndroidAppEntryPointType")

    object AndroidContextFactory : CompilerConfigurationKey<String?>("AndroidContextFactory")
    object IOSContextFactory : CompilerConfigurationKey<String?>("iOSContextFactory")
    object UnitContextFactory : CompilerConfigurationKey<String?>("UnitContextFactory")
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
    get() = get(PluginConfigurationKeys.AndroidAppEntryPointType) ?: AndroidInitializationStrategy.Composable
    set(value) = put(PluginConfigurationKeys.AndroidAppEntryPointType, value)

var CompilerConfiguration.androidContextFactory: String?
    get() = get(PluginConfigurationKeys.AndroidContextFactory)
    set(value) = putIfNotNull(PluginConfigurationKeys.AndroidContextFactory, value)

var CompilerConfiguration.iOSContextFactory: String?
    get() = get(PluginConfigurationKeys.IOSContextFactory)
    set(value) = putIfNotNull(PluginConfigurationKeys.IOSContextFactory, value)

var CompilerConfiguration.unitContextFactory: String?
    get() = get(PluginConfigurationKeys.UnitContextFactory)
    set(value) = putIfNotNull(PluginConfigurationKeys.UnitContextFactory, value)
