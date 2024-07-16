package co.touchlab.kmp.testing.framework.compiler.util

import org.jetbrains.kotlin.name.FqName

val FqName.partiallyQualifiedName: String
    get() = asString().split('.')
        .dropWhile { it.first().isLowerCase() }
        .joinToString(".")
