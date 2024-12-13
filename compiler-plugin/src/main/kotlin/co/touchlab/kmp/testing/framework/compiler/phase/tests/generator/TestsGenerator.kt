package co.touchlab.kmp.testing.framework.compiler.phase.tests.generator

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteInstanceDescriptor
import co.touchlab.kmp.testing.framework.compiler.setup.config.TestSuiteKind

interface TestsGenerator {

    val kind: TestSuiteKind

    fun generate(testsSuiteInstanceDescriptor: TestsSuiteInstanceDescriptor)
}
