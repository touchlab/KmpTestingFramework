package co.touchlab.kmp.testing.framework.compiler.phase.tests.generator

import co.touchlab.kmp.testing.framework.compiler.phase.tests.descriptor.TestsSuiteDescriptor

interface TestsEntryPointGenerator {

    fun generate(testsSuiteDescriptor: TestsSuiteDescriptor)
}
