import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("build.common")

    kotlin("jvm")
}

val libs = the<LibrariesForLibs>()

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())

    sourceSets.main {
        languageSettings.enableLanguageFeature("ContextReceivers")
    }
}
