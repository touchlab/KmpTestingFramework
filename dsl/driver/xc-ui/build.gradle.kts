plugins {
    id("build.multiplatform.all")
    id("build.publish-multiplatform")
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
