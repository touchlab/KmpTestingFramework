plugins {
    id("build.jvm")
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)

    implementation(projects.dsl)
}
