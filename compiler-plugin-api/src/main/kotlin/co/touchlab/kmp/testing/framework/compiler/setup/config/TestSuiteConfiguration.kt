package co.touchlab.kmp.testing.framework.compiler.setup.config

import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.Path

@Serializable
data class TestSuiteConfiguration(
    val name: String,
    val kind: TestSuiteKind,
    val outputDirectory: String,
    val testClass: TestClassConfiguration = TestClassConfiguration(),
) {

    val outputDirectoryPath: Path
        get() = Path(outputDirectory)
}
