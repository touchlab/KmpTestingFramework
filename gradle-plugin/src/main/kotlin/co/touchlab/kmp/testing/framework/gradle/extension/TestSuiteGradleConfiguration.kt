package co.touchlab.kmp.testing.framework.gradle.extension

import co.touchlab.kmp.testing.framework.compiler.setup.config.TestSuiteConfiguration
import co.touchlab.kmp.testing.framework.compiler.setup.config.TestSuiteKind
import org.gradle.api.Named
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

class TestSuiteGradleConfiguration(
    private val nameProperty: String,
    objects: ObjectFactory,
) : Named {

    val kind: Property<TestSuiteKind> = objects.property(TestSuiteKind::class.java)

    val outputDirectory: DirectoryProperty = objects.directoryProperty()

    private val testClass = TestClassGradleConfiguration(objects)

    override fun getName(): String = nameProperty

    fun testClass(configuration: TestClassGradleConfiguration.() -> Unit) {
        testClass.configuration()
    }

    internal fun toSerializable(): TestSuiteConfiguration =
        TestSuiteConfiguration(
            name = nameProperty,
            kind = kind.orNull ?: error("Test suite '$nameProperty' does not have a kind specified."),
            outputDirectory = outputDirectory.orNull?.asFile?.absolutePath
                ?: error("Test suite '$nameProperty' does not have an output directory specified."),
            testClass = testClass.toSerializable(),
        )
}
