package co.touchlab.kmp.testing.framework.gradle.extension

import co.touchlab.kmp.testing.framework.compiler.setup.config.TestClassConfiguration
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

class TestClassGradleConfiguration(objects: ObjectFactory) {

    val additionalImports: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    val contextFactory: Property<String> = objects.property(String::class.java)

    val additionalClassContent: Property<String> = objects.property(String::class.java).convention("")

    internal fun toSerializable(): TestClassConfiguration =
        TestClassConfiguration(
            additionalImports = additionalImports.get(),
            contextFactory = contextFactory.orNull,
            additionalClassContent = additionalClassContent.get(),
        )
}
