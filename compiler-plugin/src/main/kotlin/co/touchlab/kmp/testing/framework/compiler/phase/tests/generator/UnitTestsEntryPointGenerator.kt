package co.touchlab.kmp.testing.framework.compiler.phase.tests.generator

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.ContractDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.DriverDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteInstanceDescriptor
import co.touchlab.kmp.testing.framework.compiler.util.SmartStringBuilder
import co.touchlab.kmp.testing.framework.compiler.util.escapedKotlinIdentifierIfNeeded
import co.touchlab.kmp.testing.framework.compiler.util.getFqName
import java.nio.file.Path

class UnitTestsEntryPointGenerator(
    outputDirectory: Path,
) : BaseTestsEntryPointGenerator(outputDirectory) {

    override val instanceNamePrefix: String = "Unit"

    override val TestsSuiteDescriptor.typedDrivers: List<DriverDescriptor>
        get() = unitDrivers

    override val TestsSuiteInstanceDescriptor.generatedFileName: String
        get() = "$name.kt"

    context(SmartStringBuilder)
    override fun TestsSuiteInstanceDescriptor.appendClassHeader() {
        val packageName = getFqName(contracts.packageName, "generated")

        +"package $packageName"
        +""

        getRequiredImports(packageName).forEach {
            +"import $it"
        }
        +"import kotlin.test.Test"
        +""

        +"""class $name {
            
            """.trimIndent()
    }

    context(SmartStringBuilder)
    override fun TestsSuiteInstanceDescriptor.appendHelperMethods() {
        +"""    
            private fun runTest(action: ${contracts.contractsClassPartiallyQualifiedName}.() -> Unit) {
                val driver = ${driver.partiallyQualifiedName}()
            
                val suite = ${contracts.suiteName}(driver)
        
                val contracts = suite.${contracts.contractsClassName}()
        
                contracts.action()
            }
            """.trimIndent()
    }

    context(SmartStringBuilder)
    override fun ContractDescriptor.Simple.appendTest() {
        appendRawTest(contractFunctionName, testName, "")
    }

    context(SmartStringBuilder)
    override fun ContractDescriptor.Parametrized.appendTest() {
        dataProvider.entries.forEach { entry ->
            val dataAccess = dataProvider.partiallyQualifiedName + "." + entry.propertyName.escapedKotlinIdentifierIfNeeded() + "."

            appendRawTest(contractFunctionName, entry.testName, dataAccess)
        }
    }

    private fun SmartStringBuilder.appendRawTest(contractFunctionName: String, testName: String, dataParameter: String) {
        +"""
        @Test
        fun ${testName.escapedKotlinIdentifierIfNeeded()}() = runTest {
            $dataParameter${contractFunctionName.escapedKotlinIdentifierIfNeeded()}()
        }
        
        """.trimIndent()
    }
}
