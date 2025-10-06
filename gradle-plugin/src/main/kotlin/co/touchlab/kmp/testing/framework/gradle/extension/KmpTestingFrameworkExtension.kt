package co.touchlab.kmp.testing.framework.gradle.extension

import co.touchlab.kmp.testing.framework.compiler.setup.config.FrameworkConfiguration
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class KmpTestingFrameworkExtension @Inject constructor(
    private val objects: ObjectFactory,
) {

    val testSuites: NamedDomainObjectSet<TestSuiteGradleConfiguration> =
        objects.namedDomainObjectSet(TestSuiteGradleConfiguration::class.java)

    fun testSuite(name: String, configuration: TestSuiteGradleConfiguration.() -> Unit) {
        testSuites.findByName(name)?.let {
            it.configuration()

            return
        }

        val testSuiteConfiguration = TestSuiteGradleConfiguration(name, objects)

        testSuites.add(testSuiteConfiguration)

        testSuiteConfiguration.configuration()
    }

    internal fun toSerializable(): FrameworkConfiguration =
        FrameworkConfiguration(
            testSuites = testSuites.map { it.toSerializable() }
        )
}
