plugins {
    id("build.multiplatform.all")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.dsl.driver)
            }
        }
    }
}
