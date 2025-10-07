import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("build.common")

    kotlin("multiplatform")
}

val libs = the<LibrariesForLibs>()

kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(libs.versions.jvmTarget.get())

            freeCompilerArgs.addAll(
                "-Xjdk-release=${libs.versions.jvmTarget.get().toInt()}",
            )
        }
    }
}
