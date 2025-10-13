@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.maven.publish)
}

val vserionConfigure: KotlinCommonCompilerOptions.() -> Unit = {
    languageVersion = KotlinVersion.KOTLIN_2_0
    apiVersion = KotlinVersion.KOTLIN_2_0
    if (this is KotlinJvmCompilerOptions) {
        jvmTarget = JvmTarget.JVM_11
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions(vserionConfigure)
}

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()
    compilerOptions(vserionConfigure)

    androidNativeArm64()
    androidNativeX64()

    iosArm64()
    iosSimulatorArm64()
    iosX64()

    js().nodejs()

    jvm {
        compilerOptions(vserionConfigure)
    }

    linuxArm64()
    linuxX64()

    macosArm64()
    macosX64()

    mingwX64()

    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()

    wasmJs().nodejs()
    wasmWasi().nodejs()

    watchosArm64()
    watchosDeviceArm64()
    watchosSimulatorArm64()
    watchosX64()
}

mavenPublishing {
    coordinates(project.group.toString(), project.name, project.version.toString())
    @Suppress("UNCHECKED_CAST")
    (rootProject.ext["mavenAction"] as Action<com.vanniktech.maven.publish.MavenPublishBaseExtension>).execute(this)
}
