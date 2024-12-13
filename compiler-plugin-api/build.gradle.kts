plugins {
    id("build.jvm")

    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)

    implementation(libs.kotlinx.serialization.json)
}
