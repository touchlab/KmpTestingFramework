package co.touchlab.kmp.testing.framework.gradle

import co.touchlab.kmp.testing.framework.gradle.task.CreateKmpTestingFrameworkConfigTask
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeCompilation

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

        (kotlinCompilation as? AbstractKotlinNativeCompilation)?.let {
            configureNativeCompilerClasspath(it)
        }

        return project.provider {
            listOf(
                SubpluginOption("configuration", configFile.get().get().asFile.absolutePath),
            )
        }
    }

    private fun configureNativeCompilerClasspath(nativeCompilation: AbstractKotlinNativeCompilation) {
        nativeCompilation.project.afterEvaluate {
            nativeCompilation.compileTaskProvider.configure {
                val configuration = project.configurations.getByName(KmpTestingFrameworkGradlePlugin.nativeCompilerPluginClasspath)

                compilerPluginClasspath = listOfNotNull(
                    compilerPluginClasspath,
                    configuration,
                ).reduce(FileCollection::plus)
            }
        }
    }

    override fun getCompilerPluginId(): String = "co.touchlab.kmp-testing-framework"

    override fun getPluginArtifact(): SubpluginArtifact =
        pluginArtifact

    companion object {

        val pluginArtifact: SubpluginArtifact =
            SubpluginArtifact("co.touchlab.kmp-testing-framework", "compiler-plugin", BuildConfig.VERSION)

        val pluginArtifactCoordinates: String = pluginArtifact.run { "$groupId:$artifactId:$version" }
    }
}
