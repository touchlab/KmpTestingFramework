@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package co.touchlab.kmp.testing.framework.compiler.phase.tests

import co.touchlab.kmp.testing.framework.compiler.setup.xcTestGeneratorOutputPath
import co.touchlab.kmp.testing.framework.compiler.util.SmartStringBuilder
import co.touchlab.kmp.testing.framework.compiler.util.toValidSwiftIdentifier
import co.touchlab.kmp.testing.framework.dsl.ContractsDsl
import co.touchlab.kmp.testing.framework.dsl.driver.IOSDriver
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.isSubclassOf
import org.jetbrains.kotlin.ir.util.superClass
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import kotlin.io.path.writeText

class XCTestCaseGenerator(
    pluginContext: IrPluginContext,
    configuration: CompilerConfiguration,
) {

    private val baseOutputPath = configuration.xcTestGeneratorOutputPath

    private val contractsDslSymbol = pluginContext.referenceClass(ClassId.topLevel(FqName(ContractsDsl::class.qualifiedName!!)))

    private val iosDriverAnnotationSymbol =
        pluginContext.referenceClass(ClassId.topLevel(FqName(IOSDriver::class.qualifiedName!!)))

    init {
        requireNotNull(contractsDslSymbol) { "ContractsDsl class not found." }
    }

    fun generate(moduleFragment: IrModuleFragment) {
        val testClassesProviderVisitor = TestClassesProviderVisitor()

        moduleFragment.acceptVoid(testClassesProviderVisitor)

        testClassesProviderVisitor.discoveredContracts.forEach { contractClass ->
            val drivers = testClassesProviderVisitor.getDriversForContract(contractClass)

            drivers.forEach { driver ->
                val testSuiteInstance = TestSuiteInstance(contractClass, driver, drivers.size > 1)

                testSuiteInstance.generateTestCase()
            }
        }
    }

    private fun TestSuiteInstance.generateTestCase() {
        val fileContentBuilder = FileContentBuilder(this)

        val fileContent = fileContentBuilder.build()

        val file = baseOutputPath.resolve(generatedFileName)

        file.writeText(fileContent)
    }

    data class TestSuiteInstance(val contractClass: IrClass, val driver: IrClass, val suiteHasMultipleDrivers: Boolean) {

        val kotlinTestSuiteName = (this.contractClass.parent as IrClass).name.identifier

        val contractNestedName = "${kotlinTestSuiteName}.${contractClass.name.identifier}"

        val driverName = driver.name.identifier

        val generatedContractClassName: String =
            run {
                val baseName = "IOS$kotlinTestSuiteName"

                return@run if (suiteHasMultipleDrivers) {
                    baseName + "_" + driver.name.identifier
                } else {
                    baseName
                }
            }

        val generatedFileName: String = "${generatedContractClassName}_generated.swift"
    }

    inner class TestClassesProviderVisitor : IrElementVisitorVoid {

        val discoveredContracts: MutableList<IrClass> = mutableListOf()
        val discoveredDrivers: MutableList<IrClass> = mutableListOf()

        fun getDriversForContract(contract: IrClass): List<IrClass> {
            val baseDriver = contract.getContractBaseDriver()

            return discoveredDrivers.filter { it.isSubclassOf(baseDriver) }
        }

        private fun IrClass.getContractBaseDriver(): IrClass =
            (parent as IrClass).superClass!!.declarations.filterIsInstance<IrProperty>()
                .single { it.name.identifier == "driver" }
                .getter!!.returnType.classOrNull!!.owner

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
                discoveredDrivers.add(declaration)
            }
        }

        private fun IrClass.isContract(): Boolean =
            this.superTypes.any { it.classOrNull == contractsDslSymbol }

        private fun IrClass.isIOSDriver(): Boolean =
            this.annotations.any { it.type.classOrNull == iosDriverAnnotationSymbol }
    }

    private inner class FileContentBuilder(
        private val testSuiteInstance: TestSuiteInstance,
    ) {

        fun build(): String =
            SmartStringBuilder {
                +"""
                // Generated file

                import XCTest
                import KotlinAcceptanceTests

                class ${testSuiteInstance.generatedContractClassName} : XCTestCase {
                
                """.trimIndent()

                indented {
                    addTests()
                }

                +"""    
                    override func setUpWithError() throws {
                        continueAfterFailure = false
                    }
                
                    private func runTest(action: (${testSuiteInstance.contractNestedName}) throws -> Void) rethrows {
                        let app = XCUIApplication()
                                
                        app.launch()
                                
                        let driver = ${testSuiteInstance.driverName}(app: app)
                                
                        let suite = ${testSuiteInstance.kotlinTestSuiteName}(driver: driver)
                                
                        let contracts = ${testSuiteInstance.contractNestedName}(suite)
                                
                        try action(contracts)
                    }       
                }
                """.trimIndent()
            }

        private fun SmartStringBuilder.addTests() {
            testSuiteInstance.contractClass.declarations
                .filterIsInstance<IrSimpleFunction>()
                .filter { !it.isFakeOverride }
                .forEach { function ->
                    addTest(function)
                }
        }

        private fun SmartStringBuilder.addTest(function: IrSimpleFunction) {
            val extensionReceiverParameter = function.extensionReceiverParameter

            if (extensionReceiverParameter != null) {
                addParametrizedTests(function, extensionReceiverParameter)
            } else {
                addSimpleTest(function)
            }
        }

        private fun SmartStringBuilder.addParametrizedTests(function: IrSimpleFunction, extensionReceiverParameter: IrValueParameter) {
            val dataClass = extensionReceiverParameter.type.classOrNull!!.owner

            dataClass
                .companionObject()!!
                .declarations
                .filterIsInstance<IrProperty>()
                .filter { it.getter?.returnType?.classOrNull == dataClass.symbol }
                .forEach { property ->
                    val testName =
                        function.name.identifier.toValidSwiftIdentifier() + "__" + property.name.identifier.toValidSwiftIdentifier()

                    val propertyAccess =
                        (dataClass.parent as IrClass).name.identifier + "." + dataClass.name.identifier + ".companion." + property.name.identifier.toValidSwiftIdentifier()

                    addRawTest(function, testName, propertyAccess)
                }
        }

        private fun SmartStringBuilder.addSimpleTest(function: IrSimpleFunction) {
            addRawTest(function, function.name.identifier.toValidSwiftIdentifier(), "")
        }

        private fun SmartStringBuilder.addRawTest(function: IrSimpleFunction, testName: String, dataParameter: String) {
            +"""
            func test__${testName}() throws {
                try runTest {
                    try $0.${function.name.identifier.toValidSwiftIdentifier()}($dataParameter)
                }
            }
            
            """.trimIndent()
        }
    }
}
