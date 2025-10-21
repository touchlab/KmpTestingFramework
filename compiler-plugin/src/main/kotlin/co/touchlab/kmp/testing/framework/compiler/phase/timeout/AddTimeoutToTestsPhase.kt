@file:Suppress("OPT_IN_USAGE")

package co.touchlab.kmp.testing.framework.compiler.phase.timeout

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.ContractDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteDescriptor
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irBlockBody
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.declarations.buildReceiverParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildValueParameter
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.patchDeclarationParents
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames

class AddTimeoutToTestsPhase(
    private val pluginContext: IrPluginContext,
) {

    private val driverGetterFunctionName = Name.identifier("__kmpTestingFrameworkDriverGetter")

    private val coroutinesTestPackage = FqName("kotlinx.coroutines.test")

    private val runTestFunctionSymbol = pluginContext.referenceFunctions(
        CallableId(coroutinesTestPackage, Name.identifier("runTest"))
    )
        .singleOrNull { symbol ->
            symbol.owner.parameters.none { it.kind == IrParameterKind.ExtensionReceiver } &&
                    symbol.owner.parameters.any {
                        it.name.asString() == "timeout" && it.type.classFqName == FqName("kotlin.time.Duration")
                    }
        } ?: error("Couldn't find runTest function, check if the coroutines test library is added as dependency.")

    private val runTestFunctionTimeoutParameter = runTestFunctionSymbol.owner.parameters.singleOrNull { it.name.asString() == "timeout" }
        ?: error("Couldn't find runTest timeout parameter, check if the coroutines test library is added as dependency.")

    private val runTestFunctionTestBodyParameter = runTestFunctionSymbol.owner.parameters.singleOrNull { it.name.asString() == "testBody" }
        ?: error("Couldn't find runTest testBody parameter, check if the coroutines test library is added as dependency.")

    private val testScopeClass = pluginContext.referenceClass(ClassId(coroutinesTestPackage, Name.identifier("TestScope")))
        ?: error("Couldn't find TestScope class, check if the coroutines test library is added as dependency.")

    private val getTimeoutPropertyGetter = pluginContext.referenceProperties(
        CallableId(FqName("co.touchlab.kmp.testing.framework.dsl.driver"), FqName("TestDriver"), Name.identifier("timeout"))
    ).singleOrNull()?.owner?.getter
        ?: error("Couldn't find TestDriver.timeout property, check if the driver module is added as dependency.")

    fun addTimeout(tests: List<TestsSuiteDescriptor>) {
        tests.forEach {
            addTimeout(it)
        }
    }

    private fun addTimeout(testsSuiteDescriptor: TestsSuiteDescriptor) {
        val driverGetter = getOrCreateDriverGetter(testsSuiteDescriptor)

        testsSuiteDescriptor.contracts.tests.forEach {
            addTimeout(it, driverGetter, testsSuiteDescriptor.testsSuiteClass)
        }
    }

    private fun getOrCreateDriverGetter(testsSuiteDescriptor: TestsSuiteDescriptor): IrSimpleFunctionSymbol {
        val dslClass = testsSuiteDescriptor.contracts.dslClass

        dslClass.declarations
            .filterIsInstance<IrSimpleFunction>()
            .firstOrNull { it.name == driverGetterFunctionName }
            ?.let { return it.symbol }

        val function = pluginContext.irFactory.addFunction(dslClass) {
            name = driverGetterFunctionName
            returnType = testsSuiteDescriptor.contracts.driverGetter.owner.returnType
            visibility = DescriptorVisibilities.PROTECTED
            origin = IrDeclarationOrigin.SYNTHETIC_ACCESSOR
        }

        val functionDispatchReceiver = function.buildReceiverParameter {
            type = dslClass.typeWith()
        }

        function.parameters = listOf(functionDispatchReceiver)

        function.body = pluginContext.irBuiltIns.createIrBuilder(function.symbol).run {
            irBlockBody(UNDEFINED_OFFSET, UNDEFINED_OFFSET) {
                +irReturn(
                    irCall(testsSuiteDescriptor.contracts.driverGetter).apply {
                        dispatchReceiver = irGet(functionDispatchReceiver)
                    }
                )
            }
        }.patchDeclarationParents(function)

        return function.symbol
    }

    private fun addTimeout(contract: ContractDescriptor, driverGetter: IrSimpleFunctionSymbol, testsSuiteClass: IrClass) {
        contract.function.body?.let {
            val outerDispatchReceiver = testsSuiteClass.thisReceiver ?: error("Tests suite class must have a dispatch receiver.")

            contract.function.body = addTimeout(contract.function, outerDispatchReceiver, driverGetter, it)
        }
    }

    private fun addTimeout(
        function: IrSimpleFunction,
        functionOuterDispatchReceiver: IrValueParameter,
        driverGetter: IrSimpleFunctionSymbol,
        body: IrBody,
    ): IrBody =
        pluginContext.irBuiltIns.createIrBuilder(function.symbol, body.startOffset, body.endOffset).run {
            irBlockBody(body) {
                +irCall(runTestFunctionSymbol).apply {
                    arguments.set(
                        parameter = runTestFunctionTimeoutParameter,
                        value = irCall(getTimeoutPropertyGetter).apply {
                            dispatchReceiver = irCall(driverGetter).apply {
                                dispatchReceiver = irGet(functionOuterDispatchReceiver)
                            }
                        },
                    )
                    arguments.set(
                        parameter = runTestFunctionTestBodyParameter,
                        value = IrFunctionExpressionImpl(
                            startOffset = startOffset,
                            endOffset = endOffset,
                            type = runTestFunctionTestBodyParameter.type,
                            function = pluginContext.irFactory.buildFun {
                                startOffset = this@apply.startOffset
                                endOffset = this@apply.endOffset
                                name = SpecialNames.ANONYMOUS
                                returnType = pluginContext.irBuiltIns.unitType
                                visibility = DescriptorVisibilities.LOCAL
                                origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                                isSuspend = true
                            }.apply {
                                this.body = body
                                parameters = listOf(
                                    buildValueParameter(this) {
                                        name = Name.identifier($$"$this$runTest")
                                        kind = IrParameterKind.ExtensionReceiver
                                        type = testScopeClass.typeWith()
                                    }
                                )
                            },
                            origin = IrStatementOrigin.LAMBDA,
                        )
                    )
                }
            }.patchDeclarationParents(function.parent)
        }
}
