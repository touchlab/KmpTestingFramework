package co.touchlab.kmp.testing.framework.compiler.util

fun getFunctionImport(functionFqName: String): String =
    getFunctionPackageName(functionFqName) + "." + getFunctionNameWithoutPackage(functionFqName).split(".").first()

fun getFunctionNameWithoutPackage(functionFqName: String): String =
    functionFqName.removePrefix(getFunctionPackageName(functionFqName) + ".")

private fun getFunctionPackageName(functionFqName: String): String =
    functionFqName.split(".").dropLast(1).takeWhile { it.firstOrNull()?.isUpperCase() != true }.joinToString(".")
