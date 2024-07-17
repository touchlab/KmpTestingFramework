package co.touchlab.kmp.testing.framework.compiler.util

fun getFqName(packageName: String, partiallyQualifiedName: String): String =
    listOf(packageName, partiallyQualifiedName)
        .filter { it.isNotBlank() }
        .joinToString(separator = ".")
