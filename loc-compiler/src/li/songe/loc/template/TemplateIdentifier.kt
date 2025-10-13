package li.songe.loc.template

import li.songe.loc.LocOptions
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*

sealed class TemplateIdentifier(val name: String) {
    abstract fun build(
        locOptions: LocOptions,
        irFile: IrFile,
        expression: IrElement,
        pathList: List<IrDeclarationWithName>,
    ): String

    data object FilePath : TemplateIdentifier("filePath") {
        override fun build(
            locOptions: LocOptions,
            irFile: IrFile,
            expression: IrElement,
            pathList: List<IrDeclarationWithName>,
        ) = irFile.path.removePrefix(locOptions.projectPath + "/")
    }

    data object FileName : TemplateIdentifier("fileName") {
        override fun build(
            locOptions: LocOptions,
            irFile: IrFile,
            expression: IrElement,
            pathList: List<IrDeclarationWithName>,
        ) = irFile.name
    }

    data object ClassName : TemplateIdentifier("className") {
        override fun build(
            locOptions: LocOptions,
            irFile: IrFile,
            expression: IrElement,
            pathList: List<IrDeclarationWithName>,
        ): String {
            val p = irFile.packageFqName.asString()
            if (p.isNotEmpty()) {
                return p + "." + irFile.packagePartClassName
            }
            return irFile.packagePartClassName
        }
    }

    data object PackageName : TemplateIdentifier("packageName") {
        override fun build(
            locOptions: LocOptions,
            irFile: IrFile,
            expression: IrElement,
            pathList: List<IrDeclarationWithName>,
        ): String {
            return irFile.packageFqName.asString()
        }
    }

    data object MethodName : TemplateIdentifier("methodName") {
        override fun build(
            locOptions: LocOptions,
            irFile: IrFile,
            expression: IrElement,
            pathList: List<IrDeclarationWithName>,
        ) = pathList.joinToString(separator = ".") { it.name.asString() }
    }


    data object LineNumber : TemplateIdentifier("lineNumber") {
        override fun build(
            locOptions: LocOptions,
            irFile: IrFile,
            expression: IrElement,
            pathList: List<IrDeclarationWithName>,
        ): String {
            return (irFile.fileEntry.getLineNumber(expression.startOffset) + 1).toString()
        }
    }

    data object ColumnNumber : TemplateIdentifier("columnNumber") {
        override fun build(
            locOptions: LocOptions,
            irFile: IrFile,
            expression: IrElement,
            pathList: List<IrDeclarationWithName>,
        ): String {
            return (irFile.fileEntry.getColumnNumber(expression.startOffset) + 1).toString()
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
