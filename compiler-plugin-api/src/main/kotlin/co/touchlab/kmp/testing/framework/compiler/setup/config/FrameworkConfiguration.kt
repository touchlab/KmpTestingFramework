package co.touchlab.kmp.testing.framework.compiler.setup.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class FrameworkConfiguration(
    val testSuites: List<TestSuiteConfiguration> = emptyList(),
) {

    init {
        testSuites.groupBy { it.name }.filter { it.value.size > 1 }.forEach {
            throw IllegalArgumentException("Test suite names must be unique. Found ${it.value.size} for ${it.key}.")
        }
    }

    fun serialize(): String =
        Json.encodeToString(serializer(), this)

    companion object {

        fun deserialize(json: String): FrameworkConfiguration = Json.decodeFromString(json)
    }
}
