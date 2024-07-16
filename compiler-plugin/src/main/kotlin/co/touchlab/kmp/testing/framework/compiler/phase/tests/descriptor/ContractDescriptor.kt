@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor

import co.touchlab.kmp.testing.framework.compiler.util.partiallyQualifiedName
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.kotlinFqName

sealed interface ContractDescriptor {

    val contractFunctionName: String

    data class Simple(
        override val contractFunctionName: String,
        val testName: String,
    ) : ContractDescriptor

    data class Parametrized(
        override val contractFunctionName: String,
        val dataProvider: DataProvider,
    ) : ContractDescriptor {

        data class DataProvider(
            val partiallyQualifiedName: String,
            val entries: List<Entry>,
        ) {

            val simpleName: String = partiallyQualifiedName.substringAfterLast(".")

            data class Entry(
                val propertyName: String,
                val testName: String,
            )
        }
    }

    companion object {

        fun from(contractFunction: IrSimpleFunction): ContractDescriptor {
            val extensionReceiverParameter = contractFunction.extensionReceiverParameter

            return if (extensionReceiverParameter != null) {
                createParametrizedContract(contractFunction, extensionReceiverParameter)
            } else {
                createSimpleContract(contractFunction)
            }
        }

        private fun createSimpleContract(contractFunction: IrSimpleFunction): Simple =
            Simple(
                testName = contractFunction.name.identifier,
                contractFunctionName = contractFunction.name.identifier,
            )

        private fun createParametrizedContract(
            contractFunction: IrSimpleFunction,
            extensionReceiverParameter: IrValueParameter,
        ): Parametrized {
            val dataProvider = createDataProvider(contractFunction, extensionReceiverParameter)

            return Parametrized(
                contractFunctionName = contractFunction.name.identifier,
                dataProvider = dataProvider,
            )
        }

        private fun createDataProvider(
            contractFunction: IrSimpleFunction,
            extensionReceiverParameter: IrValueParameter,
        ): Parametrized.DataProvider {
            val dataClass = extensionReceiverParameter.type.classOrNull?.owner
                ?: error("Contract extension receiver must be a data class with test data. Was: ${contractFunction.dumpKotlinLike()} and ${extensionReceiverParameter.dumpKotlinLike()}")

            return Parametrized.DataProvider(
                partiallyQualifiedName = dataClass.kotlinFqName.partiallyQualifiedName,
                entries = createDataProviderEntries(dataClass, contractFunction),
            )
        }

        private fun createDataProviderEntries(
            dataClass: IrClass,
            contractFunction: IrSimpleFunction,
        ): List<Parametrized.DataProvider.Entry> {
            val companion = dataClass.companionObject()
                ?: error("Test parameter data class must have a companion object which provides the test data. Was: ${dataClass.dumpKotlinLike()} and ${contractFunction.dumpKotlinLike()}")

            return companion.declarations
                .filterIsInstance<IrProperty>()
                .filter { it.getter?.returnType?.classOrNull == dataClass.symbol }
                .map { property ->
                    Parametrized.DataProvider.Entry(
                        propertyName = property.name.identifier,
                        testName = contractFunction.name.identifier + "__" + property.name.identifier,
                    )
                }
        }
    }
}
