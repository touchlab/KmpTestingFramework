package co.touchlab.kmp.testing.framework.gradle

import co.touchlab.kmp.testing.framework.gradle.extension.KmpTestingFrameworkExtension
import co.touchlab.kmp.testing.framework.gradle.task.CreateKmpTestingFrameworkConfigTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class KmpTestingFrameworkGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val extension = registerExtension()

            registerCompilerPluginAppliers()

            registerCreateKmpTestingFrameworkConfigTask(extension)
        }
    }

    private fun Project.registerExtension(): KmpTestingFrameworkExtension =
        extensions.create("kmpTestingFramework", KmpTestingFrameworkExtension::class.java)

    private fun Project.registerCompilerPluginAppliers() {
        plugins.apply(KmpTestingFrameworkKotlinCompilerPluginApplier::class.java)
    }

    private fun Project.registerCreateKmpTestingFrameworkConfigTask(extension: KmpTestingFrameworkExtension) {
        tasks.register<CreateKmpTestingFrameworkConfigTask>(CreateKmpTestingFrameworkConfigTask.name) {
            val configurationProvider = project.provider { extension.toSerializable().serialize() }

            serializedConfiguration.set(configurationProvider)

            val outputFile = layout.buildDirectory.dir("KmpTestingFramework/configuration.json").get().asFile

            outputFile.parentFile.mkdirs()

            configFile.set(outputFile)
        }
    }
}
