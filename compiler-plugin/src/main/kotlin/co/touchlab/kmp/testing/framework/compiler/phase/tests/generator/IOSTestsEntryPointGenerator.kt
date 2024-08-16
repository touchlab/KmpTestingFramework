package co.touchlab.kmp.testing.framework.compiler.phase.tests.generator

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.ContractDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.DriverDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteInstanceDescriptor
import co.touchlab.kmp.testing.framework.compiler.util.SmartStringBuilder
import co.touchlab.kmp.testing.framework.compiler.util.toValidSwiftIdentifier
import java.nio.file.Path

class IOSTestsEntryPointGenerator(
    outputDirectory: Path,
) : BaseTestsEntryPointGenerator(outputDirectory) {

    override val instanceNamePrefix: String = "IOS"

    override val TestsSuiteDescriptor.typedDrivers: List<DriverDescriptor>
        get() = iOSDrivers

    override val TestsSuiteInstanceDescriptor.generatedFileName: String
        get() = "$name.swift"

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
                        
                let driver = ${driver.partiallyQualifiedName}(app: app)
                        
                let suite = ${contracts.suiteName}(driver: driver)
                        
                let contracts = ${contracts.contractsClassPartiallyQualifiedName}(suite)
                        
                try action(contracts)
                
                ${if(driver.hasOnFinally) "driver.onFinally()" else ""}
                
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
