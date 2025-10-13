package li.songe.loc

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

sealed class ConfigKey<T : Any>(
    val name: String,
    val valueDescription: String,
    val description: String,
) : CompilerConfigurationKey<T>(name) {
    abstract fun decode(value: String): T
    fun process(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        if (option.optionName == name) {
            configuration.put(this, decode(value))
        }
    }

    val cliOption = CliOption(
        optionName = name,
        valueDescription = valueDescription,
        description = description,
    )

    class StringKey(
        name: String,
        valueDescription: String,
        description: String,
    ) : ConfigKey<String>(name, valueDescription, description) {
        override fun decode(value: String): String = URLDecoder.decode(value, StandardCharsets.UTF_8)
    }
}
