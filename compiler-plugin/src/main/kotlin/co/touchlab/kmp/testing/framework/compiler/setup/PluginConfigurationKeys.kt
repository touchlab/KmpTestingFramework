package co.touchlab.kmp.testing.framework.compiler.setup

import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import java.nio.file.Path

object PluginConfigurationKeys {

    object XcTestGeneratorOutputPath : CompilerConfigurationKey<Path>("xcTestGeneratorOutputPath")
}

var CompilerConfiguration.xcTestGeneratorOutputPath: Path
    get() = getNotNull(PluginConfigurationKeys.XcTestGeneratorOutputPath)
    set(value) = put(PluginConfigurationKeys.XcTestGeneratorOutputPath, value)
