package li.songe.loc.ir

import li.songe.loc.BuildConfig
import li.songe.loc.LocOptions
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstKind
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.util.getAnnotation
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.FqName

class SimpleIrBodyGenerator(
    val locOptions: LocOptions,
    val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    var irFile: IrFile? = null
    val annotationFqName = FqName("${BuildConfig.KOTLIN_PLUGIN_ID}.Loc")
    val stringType get() = pluginContext.irBuiltIns.stringType

    override fun visitFile(declaration: IrFile): IrFile {
        irFile = declaration
        return super.visitFile(declaration).also {
            irFile = null
        }
    }

    val pathList = mutableListOf<IrDeclarationBase>()
    private inline fun <T> walkIr(declaration: IrDeclarationBase, block: () -> T): T {
        pathList.add(declaration)
        return block().also {
            pathList.removeLast()
        }
    }

    override fun visitClass(declaration: IrClass): IrStatement {
        return walkIr(declaration) { super.visitClass(declaration) }
    }

    override fun visitAnonymousInitializer(declaration: IrAnonymousInitializer): IrStatement {
        return walkIr(declaration) { super.visitAnonymousInitializer(declaration) }
    }

    override fun visitFunction(declaration: IrFunction): IrStatement {
        return walkIr(declaration) { super.visitFunction(declaration) }
    }

    override fun visitCall(expression: IrCall): IrExpression {
        val owner = expression.symbol.owner
        if (owner.hasAnnotation(annotationFqName)) {
            val resultList = expression.arguments.mapIndexedNotNull { index, arg ->
                val p = owner.parameters[index]
                if (arg == null && p.type == stringType && p.hasAnnotation(annotationFqName)) {
                    val e = p.getAnnotation(annotationFqName)?.arguments?.firstOrNull()
                    index to if (e is IrConst && e.kind == IrConstKind.String) {
                        locOptions.getTemplate(e.value as String)
                    } else {
                        // default value
                        locOptions.getTemplate("")
                    }
                } else {
                    null
                }
            }
            if (resultList.isNotEmpty()) {
                val newExp = IrCallImpl.fromSymbolOwner(
                    expression.startOffset,
                    expression.endOffset,
                    expression.type,
                    expression.symbol,
                    expression.origin,
                    expression.superQualifierSymbol
                )
                expression.arguments.forEachIndexed { i, arg ->
                    newExp.arguments[i] = arg
                }
                for ((locIndex, locTemplate) in resultList) {
                    newExp.arguments[locIndex] = IrConstImpl.string(
                        UNDEFINED_OFFSET,
                        UNDEFINED_OFFSET,
                        stringType,
                        locTemplate.build(this, expression)
                    )
                }
                return super.visitCall(newExp)
            }
        }
        return super.visitCall(expression)
    }
}
