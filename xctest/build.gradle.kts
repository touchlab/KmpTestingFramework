@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    id("build.multiplatform.darwin")
}

kotlin {
    targets.withType<KotlinNativeTarget> {
        compilations.getByName("main") {
            if (isCInteropEnabled) {
                cinterops {
                    val xcTest by creating {
                        val xcTestPath = getXCTestPath(konanTarget.xcodeSdkName).get()
                        val umbrellaHeaderPath = xcTestPath.resolve("Headers/XCTest.h")

                        definitionFile = project.file("src/nativeInterop/cinterop/xctest.def")
                        headers(umbrellaHeaderPath)
                        extraOpts("-Xforeign-exception-mode", "objc-wrap")

                        compilerOpts("-framework", "XCTest", "-F", xcTestPath.parentFile.absolutePath)
                    }
                }
            }
        }
    }
}

fun getXCTestPath(sdk: String): Provider<File> {
    val exec = providers.exec {
        commandLine("xcrun", "--show-sdk-path", "-sdk", sdk)
    }

    return exec
        .result
        .map { result ->
            result.rethrowFailure()

            val sdkPath = exec.standardOutput.asText.get().trim()

            File(sdkPath).resolve("../../Library/Frameworks/XCTest.framework")
        }
}

val isCInteropEnabled: Boolean
    get() = properties["project.cinterop-enabled"]?.toString()?.toBoolean() ?: true

val KonanTarget.xcodeSdkName: String
    get() = when (this) {
        KonanTarget.IOS_ARM64 -> "iphoneos"
        KonanTarget.IOS_SIMULATOR_ARM64, KonanTarget.IOS_X64 -> "iphonesimulator"
        KonanTarget.MACOS_ARM64, KonanTarget.MACOS_X64 -> "macosx"
        KonanTarget.TVOS_ARM64 -> "appletvos"
        KonanTarget.TVOS_SIMULATOR_ARM64, KonanTarget.TVOS_X64 -> "appletvsimulator"
        KonanTarget.WATCHOS_ARM32, KonanTarget.WATCHOS_ARM64, KonanTarget.WATCHOS_DEVICE_ARM64 -> "watchos"
        KonanTarget.WATCHOS_SIMULATOR_ARM64, KonanTarget.WATCHOS_X64 -> "watchsimulator"
        else -> error("Unsupported target: $this")
    }
