package co.touchlab.kmp.testing.framework.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class CreateKmpTestingFrameworkConfigTask : DefaultTask() {

    @get:Input
    abstract val serializedConfiguration: Property<String>

    @get:OutputFile
    abstract val configFile: RegularFileProperty

    @TaskAction
    fun execute() {
        val file = configFile.get().asFile

        file.parentFile.mkdirs()

        val configuration = serializedConfiguration.get()

        file.writeText(configuration)
    }

    companion object {

        val name: String = "createKmpTestingFrameworkConfig"
    }
}
