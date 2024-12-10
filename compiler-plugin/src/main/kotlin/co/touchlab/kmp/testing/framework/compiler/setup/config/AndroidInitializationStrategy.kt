package co.touchlab.kmp.testing.framework.compiler.setup.config

enum class AndroidInitializationStrategy {
    Composable,
    Activity;

    companion object {

        fun byName(name: String): AndroidInitializationStrategy =
            entries.first { it.name.equals(name, ignoreCase = true) }
    }
}
