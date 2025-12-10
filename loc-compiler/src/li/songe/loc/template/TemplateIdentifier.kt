package li.songe.loc.template

import li.songe.loc.ir.SimpleIrBodyGenerator
import org.jetbrains.kotlin.backend.common.serialization.mangle.ir.isAnonymous
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*

sealed class TemplateIdentifier(val name: String) {
    abstract fun build(transformer: SimpleIrBodyGenerator, expression: IrElement): String

    data object FilePath : TemplateIdentifier("filePath") {
        override fun build(
            transformer: SimpleIrBodyGenerator,
            expression: IrElement,
        ) = transformer.irFile!!.path.removePrefix(transformer.locOptions.projectPath + "/")
    }

    data object FileName : TemplateIdentifier("fileName") {
        override fun build(
            transformer: SimpleIrBodyGenerator,
            expression: IrElement,
        ) = transformer.irFile!!.name
    }

    data object ClassName : TemplateIdentifier("className") {
        override fun build(
            transformer: SimpleIrBodyGenerator,
            expression: IrElement,
        ): String {
            val p = transformer.irFile!!.packageFqName.asString()
            if (p.isNotEmpty()) {
                return p + "." + transformer.irFile!!.packagePartClassName
            }
            return transformer.irFile!!.packagePartClassName
        }
    }

    data object PackageName : TemplateIdentifier("packageName") {
        override fun build(
            transformer: SimpleIrBodyGenerator,
            expression: IrElement,
        ): String {
            return transformer.irFile!!.packageFqName.asString()
        }
    }

    data object MethodName : TemplateIdentifier("methodName") {
        override fun build(
            transformer: SimpleIrBodyGenerator,
            expression: IrElement,
        ): String {
            val sb = mutableListOf<String>()
            transformer.pathList.forEach { base ->
                val name = when (base) {
                    is IrAnonymousInitializer -> "<init>"
                    is IrConstructor -> "<constructor>"
                    is IrDeclarationWithName -> if (base.name.isAnonymous) "" else base.name.asString()
                    else -> ""
                }
                if (name.isNotEmpty()) {
                    sb.add(name)
                }
            }
            return sb.joinToString(separator = ".")
        }
    }


    data object LineNumber : TemplateIdentifier("lineNumber") {
        override fun build(
            transformer: SimpleIrBodyGenerator,
            expression: IrElement,
        ): String {
            return (transformer.irFile!!.fileEntry.getLineNumber(expression.startOffset) + 1).toString()
        }
    }

    data object ColumnNumber : TemplateIdentifier("columnNumber") {
        override fun build(
            transformer: SimpleIrBodyGenerator,
            expression: IrElement,
        ): String {
            return (transformer.irFile!!.fileEntry.getColumnNumber(expression.startOffset) + 1).toString()
        }
    }

    companion object {
        val all by lazy {
            listOf(
                FilePath,
                ClassName,
                PackageName,
                FileName,
                LineNumber,
                ColumnNumber,
                MethodName,
            )
        }
    }
}
