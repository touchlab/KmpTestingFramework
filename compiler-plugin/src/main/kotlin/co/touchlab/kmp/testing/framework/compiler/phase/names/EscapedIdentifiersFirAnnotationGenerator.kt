package co.touchlab.kmp.testing.framework.compiler.phase.names

import co.touchlab.kmp.testing.framework.compiler.util.toValidSwiftIdentifier
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.isLocalMember
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.FirMemberDeclaration
import org.jetbrains.kotlin.fir.declarations.utils.nameOrSpecialName
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension
import org.jetbrains.kotlin.fir.resolve.fqName
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.ConstantValueKind

class EscapedIdentifiersFirAnnotationGenerator(session: FirSession) : FirStatusTransformerExtension(session) {

    private val objcNameAnnotationFqName = FqName("kotlin.native.ObjCName")

    private val objcNameAnnotationSymbol by lazy {
        session.symbolProvider.getClassLikeSymbolByClassId(ClassId.topLevel(objcNameAnnotationFqName))
    }

    private val jvmNameAnnotationFqName = FqName("kotlin.jvm.JvmName")

    private val jvmNameAnnotationSymbol by lazy {
        session.symbolProvider.getClassLikeSymbolByClassId(ClassId.topLevel(jvmNameAnnotationFqName))
    }

    override fun transformStatus(status: FirDeclarationStatus, declaration: FirDeclaration): FirDeclarationStatus {
        if (declaration !is FirMemberDeclaration) {
            return status
        }

        val validSwiftIdentifier = declaration.nameOrSpecialName.asString().toValidSwiftIdentifier()

        val objCNameAnnotation = declaration.createObjCNameAnnotationIfNeeded(validSwiftIdentifier)
        val jvmNameAnnotation = declaration.createJvmNameAnnotationIfNeeded(validSwiftIdentifier)

        val newAnnotations = declaration.annotations + listOfNotNull(objCNameAnnotation, jvmNameAnnotation)

        declaration.replaceAnnotations(newAnnotations)

        return status
    }

    private fun FirDeclaration.createObjCNameAnnotationIfNeeded(validSwiftIdentifier: String): FirAnnotation? {
        val objcAnnotationSymbol = objcNameAnnotationSymbol ?: return null

        if (annotations.any { it.fqName(session) == objcNameAnnotationFqName }) {
            return null
        }

        return buildNameAnnotation(validSwiftIdentifier, objcAnnotationSymbol)
    }

    private fun FirDeclaration.createJvmNameAnnotationIfNeeded(validSwiftIdentifier: String): FirAnnotation? {
        val jvmNameAnnotationSymbol = jvmNameAnnotationSymbol ?: return null

        if (annotations.any { it.fqName(session) == jvmNameAnnotationFqName }) {
            return null
        }

        return buildNameAnnotation(validSwiftIdentifier, jvmNameAnnotationSymbol)
    }

    private fun buildNameAnnotation(
        name: String,
        annotationSymbol: FirClassLikeSymbol<*>,
    ): FirAnnotation =
        buildAnnotation {
            annotationTypeRef = annotationSymbol.constructType(emptyArray(), false).toFirResolvedTypeRef()
            argumentMapping = buildAnnotationArgumentMapping {
                mapping[Name.identifier("name")] = buildLiteralExpression(
                    source = null,
                    kind = ConstantValueKind.String,
                    value = name,
                    setType = true,
                )
            }
        }

    override fun needTransformStatus(declaration: FirDeclaration): Boolean {
        if (declaration !is FirMemberDeclaration || declaration.isLocalMember) {
            return false
        }

        val originalName = declaration.nameOrSpecialName.asString()
        val validSwiftIdentifier = originalName.toValidSwiftIdentifier()

        return originalName != validSwiftIdentifier
    }
}
