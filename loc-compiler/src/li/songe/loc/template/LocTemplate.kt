package li.songe.loc.template

import li.songe.loc.LocOptions
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrFile

data class LocTemplate(val value: String) {
    // List<Char | TemplateIdentifier>
    private val data = run {
        var i = 0
        val list = mutableListOf<Any>()
        while (i < value.length) {
            val c = value[i]
            if (c == '{') {
                val end = value.indexOf('}', i + 1)
                if (end != -1) {
                    val name = value.substring(i + 1, end)
                    val identifier = TemplateIdentifier.all.find { it.name == name }
                    if (identifier != null) {
                        list.add(identifier)
                        i = end + 1
                        continue
                    }
                }
            }
            list.add(c)
            i++
        }
        list
    }

    fun build(
        locOptions: LocOptions,
        irFile: IrFile,
        expression: IrElement,
        pathList: List<IrDeclarationWithName>,
    ) = buildString {
        for (part in data) {
            when (part) {
                is Char -> append(part)
                is TemplateIdentifier -> append(part.build(locOptions, irFile, expression, pathList))
                else -> error("Unknown part: $part")
            }
        }
    }
}
