@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    includeBuild("build-setup")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "KmpTestingFramework"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":compiler-plugin")
include(":compiler-plugin-api")
include(":dsl")
includeNested(":dsl:driver")
includeNested(":dsl:driver:xc-ui")
include(":xctest")
include(":gradle-plugin")

fun includeNested(path: String) {
    include(path)

    project(path).name = path.removePrefix(":").replace(":", "-")
}
