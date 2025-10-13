import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.maven.publish)
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        languageVersion = KotlinVersion.KOTLIN_2_0
        apiVersion = KotlinVersion.KOTLIN_2_0
    }
}

dependencies {
    implementation(libs.kotlin.gradle.plugin.api)
}

buildConfig {
    packageName(project.group.toString())
    listOf(
        "KOTLIN_PLUGIN_ID",
        "KOTLIN_PLUGIN_GROUP",
        "KOTLIN_PLUGIN_NAME",
        "KOTLIN_PLUGIN_VERSION",
    ).forEach { key ->
        buildConfigField("String", key, rootProject.ext[key] as String)
    }
}

gradlePlugin {
    plugins {
        val action = Action<PluginDeclaration> {
            id = project.group.toString()
            displayName = "Loc Gradle Plugin"
            description = "A plugin for Loc functionality"
            implementationClass = "li.songe.loc.LocGradlePlugin"
        }
        create("Loc", action)
    }
}

mavenPublishing {
    coordinates(project.group.toString(), project.name, project.version.toString())
    @Suppress("UNCHECKED_CAST")
    (rootProject.ext["mavenAction"] as Action<com.vanniktech.maven.publish.MavenPublishBaseExtension>).execute(this)
}
