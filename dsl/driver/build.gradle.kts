plugins {
    id("build.multiplatform.all")
    id("build.publish-multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}
