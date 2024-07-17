package co.touchlab.kmp.testing.framework.compiler.phase.tests.generator

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.ContractDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.DriverDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteInstanceDescriptor
import co.touchlab.kmp.testing.framework.compiler.util.SmartStringBuilder
import co.touchlab.kmp.testing.framework.compiler.util.escapedKotlinIdentifierIfNeeded
import co.touchlab.kmp.testing.framework.compiler.util.getFqName
import co.touchlab.kmp.testing.framework.compiler.util.toValidSwiftIdentifier
import java.nio.file.Path

class AndroidTestsEntryPointGenerator(
    outputDirectory: Path,
    private val androidAppEntryPoint: String,
) : BaseTestsEntryPointGenerator(outputDirectory) {

    override val instanceNamePrefix: String = "Android"

    override val TestsSuiteDescriptor.typedDrivers: List<DriverDescriptor>
        get() = androidDrivers

    override val TestsSuiteInstanceDescriptor.generatedFileName: String
        get() = "$name.kt"

    context(SmartStringBuilder)
    override fun TestsSuiteInstanceDescriptor.appendClassHeader() {
        val packageName = getFqName(contracts.packageName, "generated")

        +"""
            @file:Suppress("IllegalIdentifier")
            
            package $packageName
            
        """.trimIndent()

        getRequiredImports(packageName).forEach {
            +"import $it"
        }
        +"""
            import $androidAppEntryPoint
            import androidx.compose.ui.test.junit4.createComposeRule
            import org.junit.Rule
            import org.junit.Test
            
        """.trimIndent()

        +"""class $name {
            
            """.trimIndent()
    }

    context(SmartStringBuilder)
    override fun TestsSuiteInstanceDescriptor.appendHelperMethods() {
        +"""
            @get:Rule
            val composeTestRule = createComposeRule()

            private inline fun runTest(action: ${contracts.contractsClassPartiallyQualifiedName}.() -> Unit) {
               composeTestRule.setContent {
                    ${androidAppEntryPoint.substringAfterLast(".")}()
                }
                
                val driver = ${driver.partiallyQualifiedName}(composeTestRule)
            
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
        fun ${testName.toValidSwiftIdentifier()}() = runTest {
            $dataParameter${contractFunctionName.escapedKotlinIdentifierIfNeeded()}()
        }
        
        """.trimIndent()
    }
}
