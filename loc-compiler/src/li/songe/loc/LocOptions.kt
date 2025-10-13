package li.songe.loc

import li.songe.loc.template.LocTemplate
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrFile

data class LocOptions(
    val projectPath: String,
    val template: String,
) {
    private val actualTemplate = LocTemplate(template)
    fun buildTemplate(irFile: IrFile, expression: IrElement, pathList: List<IrDeclarationWithName>): String {
        return actualTemplate.build(this, irFile, expression, pathList)
    }

    companion object {
        val projectPath = ConfigKey.StringKey(
            name = "projectPath",
            valueDescription = "<path>",
            description = "The project path",
        )
        val template = ConfigKey.StringKey(
            name = "template",
            valueDescription = "<template>",
            description = "The template for LOC generation",
        )
        val allFields by lazy {
            listOf(
                projectPath,
                template,
            )
        }

        fun build(configuration: CompilerConfiguration): LocOptions = LocOptions(
            projectPath = configuration.get(projectPath) ?: error("Project path not set"),
            template = configuration.get(template) ?: error("Template not set"),
        )
    }
}
