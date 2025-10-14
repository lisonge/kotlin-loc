import java.util.*

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.buildconfig)
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.reader()?.use { load(it) }
}

allprojects {
    group = "li.songe.loc"
    version = "0.2.0" + (localProperties["loc.version.postfix"] ?: "")
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

fun jsonString(value: Any): String = "\"$value\""

ext {
    this["KOTLIN_PLUGIN_ID"] = jsonString(rootProject.group)
    this["KOTLIN_PLUGIN_GROUP"] = jsonString(rootProject.group)
    this["KOTLIN_PLUGIN_VERSION"] = jsonString(rootProject.version)
    this["KOTLIN_PLUGIN_NAME"] = jsonString(project(":loc-compiler").name)

    this["mavenAction"] = Action<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
        if (properties.contains("signing.keyId")) {
            publishToMavenCentral()
            signAllPublications()
        }
        val repoUrl = "https://github.com/lisonge/kotlin-loc"
        pom {
            name.set("Kotlin Loc")
            description.set("Kotlin Loc library")
            url.set(repoUrl)
            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    name.set("lisonge")
                    email.set("i@songe.li")
                    url.set("https://github.com/lisonge")
                }
            }
            scm {
                url.set(repoUrl)
            }
        }
    }
}
