package co.touchlab.kmp.testing.framework.compiler.phase.tests.generator

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.ContractDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.DriverDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteInstanceDescriptor
import co.touchlab.kmp.testing.framework.compiler.util.SmartStringBuilder
import co.touchlab.kmp.testing.framework.compiler.util.getFunctionNameWithoutPackage
import co.touchlab.kmp.testing.framework.compiler.util.toValidSwiftIdentifier
import java.nio.file.Path

class IOSTestsEntryPointGenerator(
    outputDirectory: Path,
    contextFactoryFunction: String?,
) : BaseTestsEntryPointGenerator(outputDirectory) {

    override val instanceNamePrefix: String = "IOS"

    override val TestsSuiteDescriptor.typedDrivers: List<DriverDescriptor>
        get() = iOSDrivers

    override val TestsSuiteInstanceDescriptor.generatedFileName: String
        get() = "$name.swift"

    private val contextFactoryFunction = contextFactoryFunction ?: "DefaultIOSTestContext"

    context(SmartStringBuilder)
    override fun TestsSuiteInstanceDescriptor.appendClassHeader() {
        +"""
            import XCTest
            import KotlinAcceptanceTests

            class $name : XCTestCase {
            
            """.trimIndent()
    }

    context(SmartStringBuilder)
    override fun TestsSuiteInstanceDescriptor.appendHelperMethods() {
        +"""
            override func setUpWithError() throws {
                continueAfterFailure = false
            }
        
            private func runTest(action: (${contracts.contractsClassPartiallyQualifiedName}) throws -> Void) rethrows {
                let app = XCUIApplication()
                
                let context = ${getFunctionNameWithoutPackage(contextFactoryFunction)}(app: app)
                        
                let driver = ${driver.partiallyQualifiedName}(context: context)
                
                driver.beforeTest()
                
                defer {
                    driver.afterTest()
                }
                        
                let suite = ${contracts.suiteName}(driver: driver)
                        
                let contracts = ${contracts.contractsClassPartiallyQualifiedName}(suite)
                        
                try action(contracts)
                
                app.terminate()
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
            val propertyAccess = dataProvider.partiallyQualifiedName + ".companion." + entry.propertyName.toValidSwiftIdentifier()

            appendRawTest(contractFunctionName, entry.testName, propertyAccess)
        }
    }

    private fun SmartStringBuilder.appendRawTest(contractFunctionName: String, testName: String, dataParameter: String) {
        +"""
        func test__${testName.toValidSwiftIdentifier()}() throws {
            try runTest {
                try $0.${contractFunctionName.toValidSwiftIdentifier()}($dataParameter)
            }
        }
        
        """.trimIndent()
    }
}
