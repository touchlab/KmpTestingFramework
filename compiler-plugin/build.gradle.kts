plugins {
    id("build.jvm")
    id("build.publish-jvm")
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)

    implementation(projects.compilerPluginApi)
    implementation(projects.dsl.dslDriver)
    implementation(projects.dsl)
}
