plugins {
    id("build.jvm")
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)

    implementation(projects.compilerPluginApi)
    implementation(projects.dsl.dslDriver)
    implementation(projects.dsl)
}
