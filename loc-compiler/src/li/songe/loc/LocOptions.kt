package li.songe.loc

import li.songe.loc.template.LocTemplate
import org.jetbrains.kotlin.config.CompilerConfiguration

data class LocOptions(
    val projectPath: String,
    val template: String,
) {
    val actualTemplate = LocTemplate(template)

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
