package co.touchlab.kmp.testing.framework.compiler.setup

import co.touchlab.kmp.testing.framework.compiler.setup.config.FrameworkConfiguration
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

object PluginConfigurationKeys {

    object FrameworkConfigurationKey : CompilerConfigurationKey<FrameworkConfiguration>("FrameworkConfiguration")
}

var CompilerConfiguration.frameworkConfiguration: FrameworkConfiguration
    get() = getNotNull(PluginConfigurationKeys.FrameworkConfigurationKey)
    set(value) = put(PluginConfigurationKeys.FrameworkConfigurationKey, value)
