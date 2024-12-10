package co.touchlab.kmp.testing.framework.compiler.phase.exceptions

import co.touchlab.kmp.testing.framework.dsl.ContractsDsl
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.FirTypeAlias
import org.jetbrains.kotlin.fir.dispatchReceiverClassLookupTagOrNull
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.expressions.builder.buildArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildClassReferenceExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildGetClassCall
import org.jetbrains.kotlin.fir.expressions.builder.buildVarargArgumentsExpression
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension
import org.jetbrains.kotlin.fir.resolve.firClassLike
import org.jetbrains.kotlin.fir.resolve.fqName
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.createArrayType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds

class ThrowsFirAnnotationGenerator(session: FirSession) : FirStatusTransformerExtension(session) {

    private val throwsAnnotationFqName = FqName("kotlin.Throws")
    private val jvmThrowsAnnotationFqName = FqName("kotlin.jvm.Throws")
    private val throwsAnnotationTypeRef = ClassId.topLevel(throwsAnnotationFqName).constructClassLikeType().toFirResolvedTypeRef()

    private val throwableKClassType: ConeClassLikeType = StandardClassIds.KClass
        .constructClassLikeType(arrayOf(session.builtinTypes.throwableType.coneType))

    private val contractsDslClassId = ClassId.topLevel(FqName(ContractsDsl::class.qualifiedName!!))

    private val markerClasses = setOf(contractsDslClassId)

    private val inheritsFromMarkerClassesCache = mutableMapOf<FirClassLikeDeclaration, Boolean>()

    override fun transformStatus(status: FirDeclarationStatus, declaration: FirDeclaration): FirDeclarationStatus {
        if (declaration !is FirCallableDeclaration) {
            return status
        }

        val throwsAnnotation = buildThrowsAnnotation()

        val newAnnotations = declaration.annotations + throwsAnnotation
        declaration.replaceAnnotations(newAnnotations)

        return status
    }

    private fun buildThrowsAnnotation(): FirAnnotation =
        buildAnnotation {
            annotationTypeRef = throwsAnnotationTypeRef
            argumentMapping = buildAnnotationArgumentMapping {
                mapping[Name.identifier("exceptionClasses")] = buildVarargArgumentsExpression {
                    coneElementTypeOrNull = throwableKClassType
                    coneTypeOrNull = throwableKClassType.createArrayType()
                    arguments.add(
                        buildGetClassCall {
                            coneTypeOrNull = throwableKClassType
                            argumentList = buildArgumentList {
                                arguments.add(
                                    buildClassReferenceExpression {
                                        coneTypeOrNull = session.builtinTypes.throwableType.coneType
                                        classTypeRef = session.builtinTypes.throwableType
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }

    @OptIn(SymbolInternals::class)
    override fun needTransformStatus(declaration: FirDeclaration): Boolean {
        if (declaration !is FirCallableDeclaration) {
            return false
        }

        if (declaration.annotations.any { it.isThrowsAnnotation }) {
            return false
        }

        return declaration.dispatchReceiverClassLookupTagOrNull()
            ?.toRegularClassSymbol(session)
            ?.fir
            ?.inheritsFromMarkerClasses()
            ?: false
    }

    private val FirAnnotation.isThrowsAnnotation: Boolean
        get() {
            val fqName = fqName(session)

            return fqName == throwsAnnotationFqName || fqName == jvmThrowsAnnotationFqName
        }

    private fun FirClassLikeDeclaration.inheritsFromMarkerClasses(): Boolean =
        inheritsFromMarkerClassesCache.getOrPut(this) {
            when (this) {
                is FirClass -> {
                    markerClasses.any { classId -> superTypeRefs.any { it.coneType.classId == classId } } ||
                            superTypeRefs.any { it.firClassLike(session)?.inheritsFromMarkerClasses() == true }
                }
                is FirTypeAlias -> expandedTypeRef.firClassLike(session)?.inheritsFromMarkerClasses() == true
            }
        }
}

