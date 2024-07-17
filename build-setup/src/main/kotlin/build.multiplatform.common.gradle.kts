import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("build.common")

    kotlin("multiplatform")
}

val libs = the<LibrariesForLibs>()

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
}
