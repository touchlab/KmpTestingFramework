plugins {
    id("build.jvm")
    alias(libs.plugins.buildconfig)
    `maven-publish`

    `kotlin-dsl`
}

java {
    withSourcesJar()
    withJavadocJar()
}

buildConfig {
    packageName.set("co.touchlab.kmp.testing.framework.gradle")
    className.set("BuildConfig")
    useKotlinOutput { internalVisibility = true }

    buildConfigField("VERSION", project.version.toString())
}

dependencies {
    compileOnly(libs.plugin.kotlin.gradle)

    api(projects.compilerPluginApi)
}

gradlePlugin {
    plugins.register("co.touchlab.kmp-testing-framework") {
        id = "co.touchlab.kmp-testing-framework"
        displayName = "KmpTestingFramework"
        implementationClass = "co.touchlab.kmp.testing.framework.gradle.KmpTestingFrameworkGradlePlugin"
        version = project.version
    }
    plugins.register("co.touchlab.kmp-testing-framework.kotlin-compiler-plugin-applier") {
        id = "co.touchlab.kmp-testing-framework.kotlin-compiler-plugin-applier"
        displayName = "KmpTestingFramework"
        implementationClass = "co.touchlab.kmp.testing.framework.gradle.KmpTestingFrameworkKotlinCompilerPluginApplier"
        version = project.version
    }
}

