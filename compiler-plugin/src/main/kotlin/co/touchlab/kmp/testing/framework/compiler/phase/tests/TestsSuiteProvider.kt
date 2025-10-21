@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package co.touchlab.kmp.testing.framework.compiler.phase.tests

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.ContractsDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.DriverDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteDescriptor
import co.touchlab.kmp.testing.framework.compiler.setup.config.FrameworkConfiguration
import co.touchlab.kmp.testing.framework.compiler.util.FrameworkClasses
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.isClassWithFqName
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.getAllSuperclasses
import org.jetbrains.kotlin.ir.util.isSubclassOf
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.superClass
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.name.FqName

class TestsSuiteProvider(
    private val frameworkConfiguration: FrameworkConfiguration,
) {

    fun findAll(moduleFragment: IrModuleFragment): List<TestsSuiteDescriptor> {
        val visitor = Visitor()

        moduleFragment.acceptVoid(visitor)

        return visitor.discoveredContracts.map { contracts ->
            val testsSuiteClass = contracts.parentAsClass

            val (baseDriver, driverGetter) = contracts.getBaseDriverAndGetter(testsSuiteClass)

            TestsSuiteDescriptor(
                contracts = ContractsDescriptor.from(contracts, driverGetter),
                drivers = visitor.discoveredDrivers.filterImplementations(baseDriver)
                    .map { DriverDescriptor.from(it, frameworkConfiguration) },
                testsSuiteClass = testsSuiteClass,
            )
        }
    }

    private fun List<IrClass>.filterImplementations(baseDriver: IrClass): List<IrClass> =
        this.filter { it.isSubclassOf(baseDriver) }

    private fun IrClass.getBaseDriverAndGetter(testsSuiteClass: IrClass): Pair<IrClass, IrSimpleFunctionSymbol> {
        val driverGetter = (testsSuiteClass.superClass ?: throwContractError())
            .declarations
            .filterIsInstance<IrProperty>()
            .singleOrNull { it.name.identifier == "driver" }
            ?.getter ?: throwContractError()

        return (driverGetter.returnType.classOrNull?.owner ?: throwContractError()) to driverGetter.symbol
    }

    private fun IrClass.throwContractError(): Nothing =
        error("Contracts class must be an inner class inside a class that inherits from a DSL which contains a driver property. Was: ${this.dumpKotlinLike()}")

    private class Visitor : IrVisitorVoid() {

        val discoveredContracts: MutableList<IrClass> = mutableListOf()

        val discoveredDrivers: MutableList<IrClass> = mutableListOf()

        override fun visitElement(element: IrElement) {
            element.acceptChildrenVoid(this)
        }

        override fun visitExpression(expression: IrExpression) {
        }

        override fun visitClass(declaration: IrClass) {
            super.visitClass(declaration)

            if (declaration.isContract()) {
                discoveredContracts.add(declaration)
            }

            if (declaration.isDriver()) {
                discoveredDrivers.add(declaration)
            }
        }

        private fun IrClass.isContract(): Boolean =
            this.isInstantiable() && this.getAllSuperclasses().any { it.isClassWithFqName(FrameworkClasses.contractsDslFqName) }

        private fun IrClass.isDriver(): Boolean =
            this.isInstantiable() && hasThisOrSuperTypeAnnotation(FrameworkClasses.TestDriverForTestSuiteFqName)

        private fun IrClass.isInstantiable(): Boolean =
            this.modality !in setOf(Modality.ABSTRACT, Modality.SEALED)

        private fun IrClass.hasThisOrSuperTypeAnnotation(fqName: FqName): Boolean =
            this.hasAnnotation(fqName) || this.getAllSuperclasses().any { it.hasAnnotation(fqName) }

        private fun IrClass.hasAnnotation(fqName: FqName): Boolean =
            this.annotations.any { it.type.classFqName == fqName }
    }
}
