package li.songe.loc

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

class LocProcessor : CommandLineProcessor {
    override val pluginId get() = BuildConfig.KOTLIN_PLUGIN_ID
    override val pluginOptions get() = LocOptions.allFields.map { it.cliOption }

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        LocOptions.allFields.forEach { it.process(option, value, configuration) }
    }
}
