package co.touchlab.kmp.testing.framework.compiler.setup

enum class AndroidInitializationStrategy {
    COMPOSABLE, ACTIVITY;

    companion object {
        fun byName(name: String): AndroidInitializationStrategy =
            entries.first { it.name.equals(name, ignoreCase = true) }
    }
}