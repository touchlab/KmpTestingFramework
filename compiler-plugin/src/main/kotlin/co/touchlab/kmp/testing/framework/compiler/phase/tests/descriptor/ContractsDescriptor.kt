@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.parentClassOrNull

data class ContractsDescriptor(
    val suiteName: String,
    val contractsClassName: String,
    val tests: List<ContractDescriptor>,
) {

    val contractsClassPartiallyQualifiedName: String = "$suiteName.$contractsClassName"

    companion object {

        fun from(contractClass: IrClass): ContractsDescriptor {
            val parentClass = contractClass.parentClassOrNull
                ?: error("Contract class must be nested in a test suite class. Was: ${contractClass.dumpKotlinLike()}")

            return ContractsDescriptor(
                suiteName = parentClass.name.identifier,
                contractsClassName = contractClass.name.identifier,
                tests = contractClass.declarations
                    .filterIsInstance<IrSimpleFunction>()
                    .filter { !it.isFakeOverride }
                    .map { ContractDescriptor.from(it) }
            )
        }
    }
}
