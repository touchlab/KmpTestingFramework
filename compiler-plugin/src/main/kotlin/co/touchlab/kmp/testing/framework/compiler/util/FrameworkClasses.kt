package co.touchlab.kmp.testing.framework.compiler.util

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

object FrameworkClasses {

    val contractsDslFqName = FqName("co.touchlab.kmp.testing.framework.dsl.ContractsDsl")
    val contractsDslClassId = ClassId.topLevel(contractsDslFqName)

    val androidDriverTypeAnnotationClassId = ClassId.topLevel(FqName("co.touchlab.kmp.testing.framework.dsl.driver.AndroidDriverType"))
    val iOSDriverTypeAnnotationClassId = ClassId.topLevel(FqName("co.touchlab.kmp.testing.framework.dsl.driver.IOSDriverType"))
    val unitDriverTypeAnnotationClassId = ClassId.topLevel(FqName("co.touchlab.kmp.testing.framework.dsl.driver.UnitDriverType"))
}
