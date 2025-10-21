package li.songe.loc.ir

import li.songe.loc.BuildConfig
import li.songe.loc.LocOptions
import li.songe.loc.template.LocTemplate
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstKind
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.fromSymbolOwner
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.FqName

class SimpleIrBodyGenerator(
    val locOptions: LocOptions,
    val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    var currentFile: IrFile? = null
    val annotationFqName = FqName("${BuildConfig.KOTLIN_PLUGIN_ID}.Loc")
    val stringType get() = pluginContext.irBuiltIns.stringType
    val templateCache = HashMap<String, LocTemplate>()
    fun getTemplate(value: String): LocTemplate {
        if (value.isEmpty()) return locOptions.actualTemplate
        return templateCache.getOrPut(value) { LocTemplate(value) }
    }

    override fun visitFile(declaration: IrFile): IrFile {
        currentFile = declaration
        return super.visitFile(declaration).also {
            currentFile = null
        }
    }

    // for methodName
    val pathList = mutableListOf<IrDeclarationWithName>()
    fun skipPath(declaration: IrDeclarationWithName): Boolean {
        return declaration.name.isSpecial || declaration.name.asString().isEmpty()
    }

    override fun visitClass(declaration: IrClass): IrStatement {
        if (skipPath(declaration)) super.visitClass(declaration)
        pathList.add(declaration)
        return super.visitClass(declaration).also {
            pathList.removeLast()
        }
    }

    override fun visitFunction(declaration: IrFunction): IrStatement {
        if (skipPath(declaration)) return super.visitFunction(declaration)
        pathList.add(declaration)
        return super.visitFunction(declaration).also {
            pathList.removeLast()
        }
    }

    override fun visitCall(expression: IrCall): IrExpression {
        val owner = expression.symbol.owner
        if (owner.hasAnnotation(annotationFqName)) {
            val resultList = expression.arguments.mapIndexedNotNull { index, arg ->
                val p = owner.parameters[index]
                if (arg == null && p.type == stringType && p.hasAnnotation(annotationFqName)) {
                    val e = p.defaultValue?.expression
                    index to if (e is IrConst && e.kind == IrConstKind.String) {
                        getTemplate(e.value as String)
                    } else {
                        locOptions.actualTemplate
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
                        locTemplate.build(locOptions, currentFile!!, expression, pathList)
                    )
                }
                return super.visitCall(newExp)
            }
        }
        return super.visitCall(expression)
    }
}
