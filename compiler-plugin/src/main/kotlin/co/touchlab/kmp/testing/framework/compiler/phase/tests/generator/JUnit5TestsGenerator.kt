package co.touchlab.kmp.testing.framework.compiler.phase.tests.generator

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteInstanceDescriptor
import co.touchlab.kmp.testing.framework.compiler.setup.config.TestSuiteKind
import co.touchlab.kmp.testing.framework.compiler.util.escapedKotlinIdentifierIfNeeded

object JUnit5TestsGenerator : BaseJUnitTestsGenerator() {

    override val kind: TestSuiteKind = TestSuiteKind.JUnit5

    override fun getDefaultImports(descriptor: TestsSuiteInstanceDescriptor): List<String> =
        super.getDefaultImports(descriptor) + "kotlin.test.Test"

    override fun getTestFunctionName(testName: String): String =
        testName.escapedKotlinIdentifierIfNeeded()
}
