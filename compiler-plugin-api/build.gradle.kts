plugins {
    id("build.jvm")
    id("build.publish-jvm")

    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)

    implementation(libs.kotlinx.serialization.json)
}
