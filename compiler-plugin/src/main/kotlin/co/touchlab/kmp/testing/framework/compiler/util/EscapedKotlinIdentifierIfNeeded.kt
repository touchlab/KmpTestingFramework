package co.touchlab.kmp.testing.framework.compiler.util

fun String.escapedKotlinIdentifierIfNeeded(): String =
    if (this != toValidSwiftIdentifier()) "`$this`" else this
