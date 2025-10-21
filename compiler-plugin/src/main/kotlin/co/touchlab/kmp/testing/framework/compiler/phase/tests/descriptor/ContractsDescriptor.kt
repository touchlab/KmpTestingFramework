@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor

import co.touchlab.kmp.testing.framework.compiler.util.getRequiredImport
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.packageFqName
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.parentClassOrNull

data class ContractsDescriptor(
    val suiteName: String,
    val contractsClassName: String,
    val packageName: String,
    val dslClass: IrClass,
    val driverGetter: IrSimpleFunctionSymbol,
    val tests: List<ContractDescriptor>,
) {

    val contractsClassPartiallyQualifiedName: String = "$suiteName.$contractsClassName"

    fun getRequiredImports(fromPackage: String): Set<String> =
        tests.flatMap { it.getRequiredImports(fromPackage) }.toSet() +
                getRequiredImport(fromPackage, packageName, suiteName)

    companion object {

        fun from(contractClass: IrClass, driverGetter: IrSimpleFunctionSymbol): ContractsDescriptor {
            val parentClass = contractClass.parentClassOrNull
                ?: error("Contract class must be nested in a test suite class. Was: ${contractClass.dumpKotlinLike()}")

            return ContractsDescriptor(
                suiteName = parentClass.name.identifier,
                contractsClassName = contractClass.name.identifier,
                packageName = parentClass.packageFqName?.asString() ?: "",
                dslClass = driverGetter.owner.parentAsClass,
                driverGetter = driverGetter,
                tests = contractClass.declarations
                    .filterIsInstance<IrSimpleFunction>()
                    .filter { !it.isFakeOverride }
                    .map { ContractDescriptor.from(it) }
            )
        }
    }
}
