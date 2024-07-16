@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package co.touchlab.kmp.testing.framework.compiler.phase.tests

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.ContractsDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.DriverDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteDescriptor
import co.touchlab.kmp.testing.framework.dsl.ContractsDsl
import co.touchlab.kmp.testing.framework.dsl.driver.IOSDriver
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.isSubclassOf
import org.jetbrains.kotlin.ir.util.superClass
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.name.FqName

class TestsSuiteProvider {

    fun findAll(moduleFragment: IrModuleFragment): List<TestsSuiteDescriptor> {
        val visitor = Visitor()

        moduleFragment.acceptVoid(visitor)

        return visitor.discoveredContracts.map { contracts ->
            val baseDriver = contracts.getBaseDriver()

            TestsSuiteDescriptor(
                contracts = ContractsDescriptor.from(contracts),
                iOSDrivers = visitor.discoveredIOSDrivers.filterImplementations(baseDriver).map { DriverDescriptor.from(it) },
            )
        }
    }

    private fun List<IrClass>.filterImplementations(baseDriver: IrClass): List<IrClass> =
        this.filter { it.isSubclassOf(baseDriver) }

    private fun IrClass.getBaseDriver(): IrClass {
        fun throwContractError(): Nothing =
            error("Contracts class must be an inner class inside a class that inherits from a DSL which contains a driver property. Was: ${this.dumpKotlinLike()}")

        val superClass = (parent as IrClass).superClass ?: throwContractError()

        val driverGetter = superClass.declarations
            .filterIsInstance<IrProperty>()
            .singleOrNull { it.name.identifier == "driver" }
            ?.getter ?: throwContractError()

        return driverGetter.returnType.classOrNull?.owner ?: throwContractError()
    }

    private class Visitor : IrElementVisitorVoid {

        private val contractsDslFqName = FqName(ContractsDsl::class.qualifiedName!!)

        private val iOSDriverAnnotationFqName = FqName(IOSDriver::class.qualifiedName!!)

        val discoveredContracts: MutableList<IrClass> = mutableListOf()

        val discoveredIOSDrivers: MutableList<IrClass> = mutableListOf()

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

            if (declaration.isIOSDriver()) {
                discoveredIOSDrivers.add(declaration)
            }
        }

        private fun IrClass.isContract(): Boolean =
            this.superTypes.any { it.classFqName == contractsDslFqName }

        private fun IrClass.isIOSDriver(): Boolean =
            this.hasAnnotation(iOSDriverAnnotationFqName)

        private fun IrClass.hasAnnotation(fqName: FqName): Boolean =
            this.annotations.any { it.type.classFqName == fqName }
    }
}
