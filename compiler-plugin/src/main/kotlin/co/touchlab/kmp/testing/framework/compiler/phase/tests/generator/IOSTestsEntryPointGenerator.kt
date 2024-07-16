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
    override fun TestsSuiteInstanceDescriptor.appendCode() {
        +"""
            // Generated file

            import XCTest
            import KotlinAcceptanceTests

            class $name : XCTestCase {
            
            """.trimIndent()

        indented {
            appendTests()

            appendHelperMethods()
        }

        +"}"
    }

    context(SmartStringBuilder)
    private fun TestsSuiteInstanceDescriptor.appendHelperMethods() {
        +"""    
            override func setUpWithError() throws {
                continueAfterFailure = false
            }
        
            private func runTest(action: (${contracts.contractsClassPartiallyQualifiedName}) throws -> Void) rethrows {
                let app = XCUIApplication()
                        
                app.launch()
                        
                let driver = ${driver.simpleName}(app: app)
                        
                let suite = ${contracts.suiteName}(driver: driver)
                        
                let contracts = ${contracts.contractsClassPartiallyQualifiedName}(suite)
                        
                try action(contracts)
            }
            """.trimIndent()
    }

    context(SmartStringBuilder)
    private fun TestsSuiteInstanceDescriptor.appendTests() {
        contracts.tests.forEach {
            it.appendTest()
        }
    }

    context(SmartStringBuilder)
    private fun ContractDescriptor.appendTest() {
        when (this) {
            is ContractDescriptor.Simple -> appendTest()
            is ContractDescriptor.Parametrized -> appendTest()
        }
    }

    context(SmartStringBuilder)
    private fun ContractDescriptor.Simple.appendTest() {
        appendRawTest(contractFunctionName, testName, "")
    }

    context(SmartStringBuilder)
    private fun ContractDescriptor.Parametrized.appendTest() {
        dataProvider.entries.forEach { entry ->
            val propertyAccess = (dataProvider.partiallyQualifiedName + ".companion." + entry.propertyName).toValidSwiftIdentifier()

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
