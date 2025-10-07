package co.touchlab.kmp.testing.framework.compiler.phase.tests.generator

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.ContractDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteInstanceDescriptor
import co.touchlab.kmp.testing.framework.compiler.setup.config.TestSuiteKind
import co.touchlab.kmp.testing.framework.compiler.util.SmartStringBuilder
import co.touchlab.kmp.testing.framework.compiler.util.toValidSwiftIdentifier

object XCTestsGenerator : BaseTestsGenerator() {

    override val kind: TestSuiteKind = TestSuiteKind.XCTest

    override val TestsSuiteInstanceDescriptor.generatedFileName: String
        get() = "$name.swift"

    override fun getDefaultImports(descriptor: TestsSuiteInstanceDescriptor): List<String> =
        listOf("XCTest")

    context(SmartStringBuilder)
    override fun TestsSuiteInstanceDescriptor.appendClassHeader() {
        +"""
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
            """.trimIndent()

        if (configuration.testClass.contextFactory != null) {
            +"""
           |    let context = ${configuration.testClass.contextFactory}
           |
           |    let driver = ${driver.partiallyQualifiedName}(context: context)
            """.trimMargin()
        } else {
            +"""
           |    let driver = ${driver.partiallyQualifiedName}()
            """.trimMargin()
        }

        +"""
                
                defer {
                    driver.afterTest()
                }
                
                driver.beforeTest()
                        
                let suite = ${contracts.suiteName}(driver: driver)
                        
                let contracts = ${contracts.contractsClassPartiallyQualifiedName}(suite)
                        
                try action(contracts)
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
