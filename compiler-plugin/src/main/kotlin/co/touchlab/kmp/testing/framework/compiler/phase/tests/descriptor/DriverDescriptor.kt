package co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor

import co.touchlab.kmp.testing.framework.compiler.util.getRequiredImport
import co.touchlab.kmp.testing.framework.compiler.util.partiallyQualifiedName
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.packageFqName

data class DriverDescriptor(
    val partiallyQualifiedName: String,
    val packageName: String,
) {

    fun getRequiredImports(fromPackage: String): Set<String> =
        getRequiredImport(fromPackage, packageName, partiallyQualifiedName)

    companion object {

        fun from(driverClass: IrClass): DriverDescriptor = DriverDescriptor(
            partiallyQualifiedName = driverClass.kotlinFqName.partiallyQualifiedName,
            packageName = driverClass.packageFqName?.asString() ?: "",
        )
    }
}
