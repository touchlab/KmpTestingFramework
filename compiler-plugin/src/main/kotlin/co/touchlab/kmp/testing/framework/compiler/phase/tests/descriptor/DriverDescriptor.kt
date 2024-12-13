package co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor

import co.touchlab.kmp.testing.framework.compiler.setup.config.FrameworkConfiguration
import co.touchlab.kmp.testing.framework.compiler.setup.config.TestSuiteConfiguration
import co.touchlab.kmp.testing.framework.compiler.util.FrameworkClasses
import co.touchlab.kmp.testing.framework.compiler.util.getRequiredImport
import co.touchlab.kmp.testing.framework.compiler.util.partiallyQualifiedName
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.getAllSuperclasses
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.packageFqName

data class DriverDescriptor(
    val testSuiteConfiguration: TestSuiteConfiguration,
    val partiallyQualifiedName: String,
    val packageName: String,
) {

    fun getRequiredImports(fromPackage: String): Set<String> =
        getRequiredImport(fromPackage, packageName, partiallyQualifiedName)

    companion object {

        fun from(driverClass: IrClass, frameworkConfiguration: FrameworkConfiguration): DriverDescriptor {
            val testSuiteName = driverClass.testSuiteName

            val testSuiteConfiguration = frameworkConfiguration.testSuites.firstOrNull { it.name == testSuiteName }
                ?: throw IllegalArgumentException("Test suite '$testSuiteName' was not configured.")

            return DriverDescriptor(
                partiallyQualifiedName = driverClass.kotlinFqName.partiallyQualifiedName,
                packageName = driverClass.packageFqName?.asString() ?: "",
                testSuiteConfiguration = testSuiteConfiguration,
            )
        }

        private val IrClass.testSuiteName: String
            get() {
                val testDriverAnnotations = (this.getAllSuperclasses() + this)
                    .flatMap { it.annotations }
                    .filter { it.type.classFqName == FrameworkClasses.TestDriverForTestSuiteFqName }

                check(testDriverAnnotations.size <= 1) {
                    "Multiple ${FrameworkClasses.TestDriverForTestSuiteFqName.asString()} annotations found for class: ${this.name}"
                }

                val testDriverAnnotation = testDriverAnnotations.firstOrNull()
                    ?: error("No ${FrameworkClasses.TestDriverForTestSuiteFqName.asString()} annotation found for class: ${this.name}")

                val nameValue = testDriverAnnotation.getValueArgument(FrameworkClasses.TestDriverForTestSuiteNameParameterIdentifier)
                    ?: error("No 'name' parameter found in ${FrameworkClasses.TestDriverForTestSuiteFqName.asString()} annotation for class: ${this.name}")

                val nameConst = (nameValue as? IrConst)
                    ?: error("Value of 'name' parameter in ${FrameworkClasses.TestDriverForTestSuiteFqName.asString()} annotation for class: ${this.name} is not a constant. Was: $nameValue")

                return nameConst.value as? String
                    ?: error("Value of 'name' parameter in ${FrameworkClasses.TestDriverForTestSuiteFqName.asString()} annotation for class: ${this.name} is not a string. Was: $nameConst")
            }
    }
}
