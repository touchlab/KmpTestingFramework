package co.touchlab.kmp.testing.framework.compiler.phase.tests.generator

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.ContractDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteInstanceDescriptor
import co.touchlab.kmp.testing.framework.compiler.util.SmartStringBuilder
import co.touchlab.kmp.testing.framework.compiler.util.escapedKotlinIdentifierIfNeeded
import co.touchlab.kmp.testing.framework.compiler.util.getFqName

abstract class BaseJUnitTestsGenerator : BaseTestsGenerator() {

    override val TestsSuiteInstanceDescriptor.generatedFileName: String
        get() = "$name.kt"

    override fun getDefaultImports(descriptor: TestsSuiteInstanceDescriptor): List<String> =
        descriptor.getRequiredImports(descriptor.packageName).toList()

    private val TestsSuiteInstanceDescriptor.packageName: String
        get() = getFqName(contracts.packageName, "generated")

    context(SmartStringBuilder, TestsSuiteInstanceDescriptor)
    override fun appendFileHeader() {
        +"package $packageName"
        +""
    }

    context(SmartStringBuilder)
    override fun TestsSuiteInstanceDescriptor.appendClassHeader() {
        +"""class $name {
            
            """.trimIndent()
    }

    context(SmartStringBuilder)
    override fun TestsSuiteInstanceDescriptor.appendHelperMethods() {
        +"""    
            private inline fun runTest(action: ${contracts.contractsClassPartiallyQualifiedName}.() -> Unit) {
            """.trimIndent()

        if (configuration.testClass.contextFactory != null) {
            +"""
           |    val context = ${configuration.testClass.contextFactory}
           |
           |    val driver = ${driver.partiallyQualifiedName}(context)
            """.trimMargin()
        } else {
            +"""
           |    val driver = ${driver.partiallyQualifiedName}()
            """.trimMargin()
        }

        +"""
                
                try {
                    driver.beforeTest()
                
                    val suite = ${contracts.suiteName}(driver)
        
                    val contracts = suite.${contracts.contractsClassName}()
        
                    contracts.action()
                } finally {
                    driver.afterTest()
                }
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
        fun ${getTestFunctionName(testName)}() = runTest {
            $dataParameter${contractFunctionName.escapedKotlinIdentifierIfNeeded()}()
        }
        
        """.trimIndent()
    }

    protected abstract fun getTestFunctionName(testName: String): String
}
