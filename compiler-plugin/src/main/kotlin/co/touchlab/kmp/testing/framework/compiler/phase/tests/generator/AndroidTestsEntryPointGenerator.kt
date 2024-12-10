package co.touchlab.kmp.testing.framework.compiler.phase.tests.generator

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.ContractDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.DriverDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteInstanceDescriptor
import co.touchlab.kmp.testing.framework.compiler.setup.config.AndroidInitializationStrategy
import co.touchlab.kmp.testing.framework.compiler.util.SmartStringBuilder
import co.touchlab.kmp.testing.framework.compiler.util.escapedKotlinIdentifierIfNeeded
import co.touchlab.kmp.testing.framework.compiler.util.getFqName
import co.touchlab.kmp.testing.framework.compiler.util.getFunctionImport
import co.touchlab.kmp.testing.framework.compiler.util.getFunctionNameWithoutPackage
import co.touchlab.kmp.testing.framework.compiler.util.toValidSwiftIdentifier
import java.nio.file.Path

class AndroidTestsEntryPointGenerator(
    outputDirectory: Path,
    private val androidAppEntryPoint: String,
    private val androidInitializationStrategy: AndroidInitializationStrategy,
    contextFactoryFunction: String?,
) : BaseTestsEntryPointGenerator(outputDirectory) {

    override val instanceNamePrefix: String = "Android"

    override val TestsSuiteDescriptor.typedDrivers: List<DriverDescriptor>
        get() = androidDrivers

    override val TestsSuiteInstanceDescriptor.generatedFileName: String
        get() = "$name.kt"

    private val contextFactoryFunction =
        contextFactoryFunction ?: "co.touchlab.kmp.testing.framework.dsl.context.AndroidTestContext.Default"

    context(SmartStringBuilder)
    override fun TestsSuiteInstanceDescriptor.appendClassHeader() {
        val packageName = getFqName(contracts.packageName, "generated")

        +"""
            @file:Suppress("IllegalIdentifier")
            
            package $packageName
            
        """.trimIndent()

        +when (androidInitializationStrategy) {
            AndroidInitializationStrategy.Composable -> "import androidx.compose.ui.test.junit4.createComposeRule"
            AndroidInitializationStrategy.Activity -> "import androidx.compose.ui.test.junit4.createEmptyComposeRule"
        }

        +"import $androidAppEntryPoint"

        getRequiredImports(packageName).sorted().forEach {
            +"import $it"
        }

        +"import ${getFunctionImport(contextFactoryFunction)}"

        +"""
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
            val composeTestRule = ${buildComposeTestRule()}

            private inline fun runTest(action: ${contracts.contractsClassPartiallyQualifiedName}.() -> Unit) {""".trimIndent()

        +content()

        +"""
            
                val driver = ${driver.partiallyQualifiedName}(context)
            
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
        fun ${testName.toValidSwiftIdentifier()}() = runTest {
            $dataParameter${contractFunctionName.escapedKotlinIdentifierIfNeeded()}()
        }
        
        """.trimIndent()
    }

    private fun buildComposeTestRule(): String =
        when (androidInitializationStrategy) {
            AndroidInitializationStrategy.Composable -> "createComposeRule()"
            AndroidInitializationStrategy.Activity -> "createEmptyComposeRule()"
        }

    private fun entryPointSimpleName(): String = androidAppEntryPoint.substringAfterLast(".")

    private fun content(): String =
        when (androidInitializationStrategy) {
            AndroidInitializationStrategy.Composable -> """
            |    composeTestRule.setContent {
            |        ${entryPointSimpleName()}()
            |    }
            |
            |    val context = ${getFunctionNameWithoutPackage(contextFactoryFunction)}(composeTestRule, null)
            """.trimMargin()
            AndroidInitializationStrategy.Activity -> """
            |    val context = ${getFunctionNameWithoutPackage(contextFactoryFunction)}(composeTestRule, ${entryPointSimpleName()}::class.java)
            """.trimMargin()
        }
}
