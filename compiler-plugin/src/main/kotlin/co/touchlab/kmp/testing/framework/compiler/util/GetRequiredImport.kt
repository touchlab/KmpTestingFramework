package co.touchlab.kmp.testing.framework.compiler.util

fun getRequiredImport(fromPackage: String, declarationPackage: String, declarationPartiallyQualifiedName: String): Set<String> =
    getFqName(declarationPackage, declarationPartiallyQualifiedName)
        .takeIf { fromPackage != declarationPackage }
        ?.let { setOf(it) }
        ?: emptySet()
