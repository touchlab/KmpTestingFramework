import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("build.common")

    kotlin("jvm")
}

val libs = the<LibrariesForLibs>()

tasks.withType<JavaCompile>().configureEach {
    options.release = libs.versions.jvmTarget.get().toInt()
}

kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())

    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(libs.versions.jvmTarget.get())

        freeCompilerArgs.addAll(
            "-Xjdk-release=${libs.versions.jvmTarget.get().toInt()}",
        )
    }

    sourceSets.main {
        languageSettings.enableLanguageFeature("ContextReceivers")
    }
}
