plugins {
    id("build.multiplatform.all")
}

kotlin {
    sourceSets {
        appleMain {
            dependencies {
                api(projects.xctest)
                implementation(projects.dsl.dslDriver)
            }
        }
    }
}
