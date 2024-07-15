package co.touchlab.kmp.testing.framework.compiler.setup

import co.touchlab.kmp.testing.framework.compiler.phase.exceptions.ThrowsFirAnnotationGenerator
import co.touchlab.kmp.testing.framework.compiler.phase.names.EscapedIdentifiersFirAnnotationGenerator
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class FirExtensionRegistrar : FirExtensionRegistrar() {

    override fun ExtensionRegistrarContext.configurePlugin() {
        +::EscapedIdentifiersFirAnnotationGenerator
        +::ThrowsFirAnnotationGenerator
    }
}
