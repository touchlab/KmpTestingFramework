package co.touchlab.kmp.testing.framework.compiler.util

import co.touchlab.kmp.testing.framework.dsl.ContractsDsl
import co.touchlab.kmp.testing.framework.dsl.driver.TestDriverForTestSuite
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object FrameworkClasses {

    val contractsDslFqName = FqName(ContractsDsl::class.qualifiedName!!)
    val contractsDslClassId = ClassId.topLevel(contractsDslFqName)

    val TestDriverForTestSuiteFqName = FqName(TestDriverForTestSuite::class.qualifiedName!!)
    val TestDriverForTestSuiteNameParameterIdentifier = Name.identifier("name")
}
