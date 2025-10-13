package li.songe.loc

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Suppress("unused")
class LocGradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun getCompilerPluginId(): String = BuildConfig.KOTLIN_PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = BuildConfig.KOTLIN_PLUGIN_GROUP,
        artifactId = BuildConfig.KOTLIN_PLUGIN_NAME,
        version = BuildConfig.KOTLIN_PLUGIN_VERSION,
    )

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val params = kotlinCompilation.target.project.extensions.findByType(LocExtension::class.java)
            ?: LocExtension()
        params.projectPath = kotlinCompilation.project.rootDir.absolutePath.replace('\\', '/')

        return kotlinCompilation.target.project.provider {
            params.entries.map { (key, value) ->
                SubpluginOption(key = key, value = URLEncoder.encode(value, StandardCharsets.UTF_8))
            }
        }
    }

    override fun apply(target: Project) {
        super.apply(target)
        target.extensions.create("loc", LocExtension::class.java)
    }
}
