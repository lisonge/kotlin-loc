package li.songe.loc

import li.songe.loc.ir.SimpleIrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

class LocRegistrar : CompilerPluginRegistrar() {
    override val supportsK2 get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val options = LocOptions.build(configuration)
        IrGenerationExtension.registerExtension(SimpleIrGenerationExtension(options))
    }
}
