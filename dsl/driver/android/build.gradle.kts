plugins {
    id("build.multiplatform.all")
}

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                api(project.dependencies.platform(libs.androidx.compose.bom))
                api(libs.androidx.compose.ui.test.junit4)
                implementation(projects.dsl.driver)
            }
        }
    }
}
