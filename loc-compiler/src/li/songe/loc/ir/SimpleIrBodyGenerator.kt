package li.songe.loc.ir

import li.songe.loc.BuildConfig
import li.songe.loc.LocOptions
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.FqName

class SimpleIrBodyGenerator(
    val locOptions: LocOptions,
    val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    var currentFile: IrFile? = null

    val constName = "<get-LOC>"
    val constLocFqName = FqName("${BuildConfig.KOTLIN_PLUGIN_ID}.$constName")
    val annotationFqName = FqName("${BuildConfig.KOTLIN_PLUGIN_ID}.Loc")
    val stringType get() = pluginContext.irBuiltIns.stringType

    override fun visitFile(declaration: IrFile): IrFile {
        currentFile = declaration
        return super.visitFile(declaration).also {
            currentFile = null
        }
    }

    val pathList = mutableListOf<IrDeclarationWithName>()

    override fun visitClass(declaration: IrClass): IrStatement {
        if (declaration.name.asString().isEmpty()) {
            return super.visitClass(declaration)
        }
        pathList.add(declaration)
        return super.visitClass(declaration).also {
            pathList.removeLast()
        }
    }

    override fun visitFunction(declaration: IrFunction): IrStatement {
        if (declaration.name.asString().isEmpty()) {
            return super.visitFunction(declaration)
        }
        pathList.add(declaration)
        return super.visitFunction(declaration).also {
            pathList.removeLast()
        }
    }


    private fun getLocArgumentIndex(expression: IrCall): Int {
        expression.arguments.forEachIndexed { i, arg ->
            if (arg == null) {
                val p = expression.symbol.owner.parameters[i]
                if (p.type == stringType && p.hasAnnotation(annotationFqName)) {
                    return i
                }
            }
        }
        return -1
    }

    override fun visitCall(expression: IrCall): IrExpression {
        val owner = expression.symbol.owner
        if (owner.name.asString() == constName && owner.fqNameWhenAvailable == constLocFqName) {
            return IrConstImpl.string(
                expression.startOffset,
                expression.endOffset,
                stringType,
                locOptions.buildTemplate(currentFile!!, expression, pathList)
            )
        }

        val locIndex = getLocArgumentIndex(expression)
        if (locIndex >= 0) {
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
            newExp.arguments[locIndex] = IrConstImpl.string(
                UNDEFINED_OFFSET,
                UNDEFINED_OFFSET,
                stringType,
                locOptions.buildTemplate(currentFile!!, expression, pathList)
            )
            return super.visitCall(newExp)
        }
        return super.visitCall(expression)
    }
}
