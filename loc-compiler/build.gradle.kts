import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
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

kotlin {
    compilerOptions {
        optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
        optIn.add("org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        languageVersion = KotlinVersion.KOTLIN_2_0
        apiVersion = KotlinVersion.KOTLIN_2_0
    }
}

buildConfig {
    packageName(group.toString())
    listOf("KOTLIN_PLUGIN_ID").forEach { key ->
        buildConfigField("String", key, rootProject.ext[key] as String)
    }
}

dependencies {
    compileOnly(libs.kotlin.compiler)
}

mavenPublishing {
    coordinates(project.group.toString(), project.name, project.version.toString())
    @Suppress("UNCHECKED_CAST")
    (rootProject.ext["mavenAction"] as Action<com.vanniktech.maven.publish.MavenPublishBaseExtension>).execute(this)
}
