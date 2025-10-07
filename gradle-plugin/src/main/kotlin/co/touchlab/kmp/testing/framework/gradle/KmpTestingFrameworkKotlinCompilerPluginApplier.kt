package co.touchlab.kmp.testing.framework.gradle

import co.touchlab.kmp.testing.framework.gradle.task.CreateKmpTestingFrameworkConfigTask
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class KmpTestingFrameworkKotlinCompilerPluginApplier : KotlinCompilerPluginSupportPlugin {

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.project

        val configFile = project.tasks.named<CreateKmpTestingFrameworkConfigTask>(CreateKmpTestingFrameworkConfigTask.name).map {
            it.configFile
        }

        kotlinCompilation.compileTaskProvider.configure {
            inputs.file(configFile)
        }

        return project.provider {
            listOf(
                SubpluginOption("configuration", configFile.get().get().asFile.absolutePath),
            )
        }
    }

    override fun getCompilerPluginId(): String = "co.touchlab.kmp-testing-framework"

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact("co.touchlab.kmp-testing-framework", "compiler-plugin", BuildConfig.VERSION)
}
