plugins {
    id("build.common")

    kotlin("jvm")
}

kotlin {
    jvmToolchain(19)

    sourceSets.main {
        languageSettings.enableLanguageFeature("ContextReceivers")
    }
}
