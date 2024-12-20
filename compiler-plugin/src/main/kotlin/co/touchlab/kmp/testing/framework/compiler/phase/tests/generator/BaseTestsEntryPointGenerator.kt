package co.touchlab.kmp.testing.framework.compiler.phase.tests.generator

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.ContractDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.DriverDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteDescriptor
import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteInstanceDescriptor
import co.touchlab.kmp.testing.framework.compiler.util.SmartStringBuilder
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeText

abstract class BaseTestsEntryPointGenerator(
    private val outputDirectory: Path,
) : TestsEntryPointGenerator {

    override fun generate(testsSuiteDescriptor: TestsSuiteDescriptor) {
        val typedDrivers = testsSuiteDescriptor.typedDrivers

        val testsSuiteInstances = typedDrivers.map { driver ->
            TestsSuiteInstanceDescriptor(
                contracts = testsSuiteDescriptor.contracts,
                driver = driver,
                suiteHasMultipleDrivers = typedDrivers.size > 1,
            )
        }

        testsSuiteInstances.forEach {
            generate(it)
        }
    }

    private fun generate(testsSuiteInstanceDescriptor: TestsSuiteInstanceDescriptor) {
        val outputFile = outputDirectory.resolve(testsSuiteInstanceDescriptor.generatedFileName)

        val fileContent = SmartStringBuilder {
            +"// This file was generated by KMP Testing Framework. Do not edit it manually."
            +""

            testsSuiteInstanceDescriptor.appendCode()
        }

        Files.createDirectories(outputFile.parent)

        outputFile.writeText(fileContent)
    }

    context(SmartStringBuilder)
    private fun TestsSuiteInstanceDescriptor.appendCode() {
        appendClassHeader()

        indented {
            appendTests()

            appendHelperMethods()
        }

        +"}"
    }

    context(SmartStringBuilder)
    protected fun TestsSuiteInstanceDescriptor.appendTests() {
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

    protected val TestsSuiteInstanceDescriptor.name: String
        get() = if (suiteHasMultipleDrivers) {
            instanceNamePrefix + contracts.suiteName + "_" + driver.partiallyQualifiedName
        } else {
            instanceNamePrefix + contracts.suiteName
        }

    protected abstract val instanceNamePrefix: String

    protected abstract val TestsSuiteDescriptor.typedDrivers: List<DriverDescriptor>

    protected abstract val TestsSuiteInstanceDescriptor.generatedFileName: String

    context(SmartStringBuilder)
    protected abstract fun TestsSuiteInstanceDescriptor.appendClassHeader()

    context(SmartStringBuilder)
    protected abstract fun TestsSuiteInstanceDescriptor.appendHelperMethods()

    context(SmartStringBuilder)
    protected abstract fun ContractDescriptor.Simple.appendTest()

    context(SmartStringBuilder)
    protected abstract fun ContractDescriptor.Parametrized.appendTest()
}
