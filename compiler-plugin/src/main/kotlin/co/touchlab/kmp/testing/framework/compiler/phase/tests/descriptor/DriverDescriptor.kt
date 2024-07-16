package co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.kotlinFqName

data class DriverDescriptor(
    val fqName: String,
) {

    val simpleName: String = fqName.substringAfterLast(".")

    companion object {

        fun from(driverClass: IrClass): DriverDescriptor = DriverDescriptor(
            fqName = driverClass.kotlinFqName.toString(),
        )
    }
}
