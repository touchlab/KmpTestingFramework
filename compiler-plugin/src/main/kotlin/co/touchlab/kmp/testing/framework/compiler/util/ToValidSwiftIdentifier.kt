package co.touchlab.kmp.testing.framework.compiler.util

fun String.toValidSwiftIdentifier(): String =
    this
        .mapIndexed { index, char ->
            when {
                char.isLetter() -> char
                char.isDigit() -> if (index == 0) "_$char" else char
                else -> "_"
            }
        }
        .joinToString("")
        .takeIf { it.isNotBlank() && it != "_" }
        ?: "__"
