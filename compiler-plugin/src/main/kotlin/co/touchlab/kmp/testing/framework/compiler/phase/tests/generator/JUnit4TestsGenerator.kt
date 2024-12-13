package co.touchlab.kmp.testing.framework.compiler.phase.tests.generator

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteInstanceDescriptor
import co.touchlab.kmp.testing.framework.compiler.setup.config.TestSuiteKind
import co.touchlab.kmp.testing.framework.compiler.util.SmartStringBuilder
import co.touchlab.kmp.testing.framework.compiler.util.toValidSwiftIdentifier

object JUnit4TestsGenerator : BaseJUnitTestsGenerator() {

    override val kind: TestSuiteKind = TestSuiteKind.JUnit4

    override fun getDefaultImports(descriptor: TestsSuiteInstanceDescriptor): List<String> =
        super.getDefaultImports(descriptor) + "org.junit.Test"

    context(SmartStringBuilder, TestsSuiteInstanceDescriptor)
    override fun appendFileHeader() {
        +"@file:Suppress(\"IllegalIdentifier\")"
        +""

        super.appendFileHeader()
    }

    override fun getTestFunctionName(testName: String): String =
        testName.toValidSwiftIdentifier()
}
