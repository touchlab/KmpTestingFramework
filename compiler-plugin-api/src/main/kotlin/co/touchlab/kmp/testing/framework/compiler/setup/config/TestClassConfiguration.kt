package co.touchlab.kmp.testing.framework.compiler.setup.config

import kotlinx.serialization.Serializable

@Serializable
data class TestClassConfiguration(
    val additionalImports: List<String> = emptyList(),
    val contextFactory: String? = null,
    val additionalClassContent: String = "",
)
