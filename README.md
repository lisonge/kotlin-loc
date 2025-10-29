# kotlin-loc

[![Maven Central](https://img.shields.io/maven-central/v/li.songe.loc/loc-compiler.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/li.songe.loc/loc-compiler)
[![License](http://img.shields.io/:License-Apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

A Kotlin compilation plugin used to replace specific variables with the current code location, which can solve the
problem of `Throwable().printStackTrace()` being unfriendly to minimization and Kotlin/Native.

```kotlin
package example

import li.songe.loc.Loc

fun main() {
    test("Hello, World!")
    // [example.ExampleKt.main(Example.kt:9)]: Hello, World!
    test2("Hello, World!")
    // [Example.kt:11]: Hello, World!
    println(Throwable().stackTraceToString())
    // see the following stack trace (jvm/mingwX64)
}

@Loc
fun test(message: String, @Loc loc: String = "") {
    println("[$loc]: $message")
}

@Loc
fun test2(message: String, @Loc("{fileName}:{lineNumber}") loc: String = "") { // custom template
    println("[$loc]: $message")
}
```

<details open>
  <summary>trace-jvm</summary>

```text
java.lang.Throwable
    at example.ExampleKt.main(Example.kt:11)
    at example.ExampleKt.main(Example.kt)
```

</details>

<details open>
  <summary>trace-android(isMinifyEnabled)</summary>

```text
java.lang.Throwable
    at a.b.c(b.kt:3)
    at a.b.c(b.kt)
```

</details>

<details open>
  <summary>trace-mingwX64</summary>

```text
kotlin.Throwable
    at 0   ???                                 7ff6baa398c6       kfun:kotlin.Throwable#<init>(){} + 70
    at 1   ???                                 7ff6baa6700e       kfun:#main(){} + 174
    at 2   ???                                 7ff6baa6726f       Konan_start + 111
    at 3   ???                                 7ff6baa85633       Init_and_run_start + 99
    at 4   ???                                 7ff6baa313b4       __tmainCRTStartup + 564
    at 5   ???                                 7ff6baa3150b       mainCRTStartup + 27
    at 6   ???                                 7ff8148ee8d7       _ZSt25__throw_bad_function_callv + 5803052231
    at 7   ???                                 7ff81548c53c       _ZSt25__throw_bad_function_callv + 5815232812
```

</details>

## Usage

```toml
# gradle/libs.versions.toml
[versions]
loc = "<version>" # https://github.com/lisonge/kotlin-loc/releases

[libraries]
loc-annotation = { module = "li.songe.loc:loc-annotation", version.ref = "loc" }

[plugins]
loc = { id = "li.songe.loc", version.ref = "loc" }
```

```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.loc) apply false
}
```

```kotlin
// example/build.gradle.kts
plugins {
    kotlin("multiplatform")
    alias(libs.plugins.loc)
}

kotlin {
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                compileOnly(libs.loc.annotation)
            }
        }
    }
}

// Optional configuration
// loc { }
```

## Configuration

```kotlin
loc {
    // the default configuration
    template = "{className}.{methodName}({fileName}:{lineNumber})"
    // output -> example.ExampleKt.main(Example.kt:6)
}
```

```kotlin
// use configuration from above
@Loc
fun test(message: String, @Loc loc: String = "") {
    println("[$loc]: $message")
}
// use custom template
@Loc
fun test2(message: String, @Loc("{fileName}:{lineNumber}") loc: String = "") {
    println("[$loc]: $message")
}
```

| template         | example                                            |
|------------------|----------------------------------------------------|
| `{filePath}`     | `example/src/commonMain/kotlin/example/Example.kt` |
| `{fileName}`     | `Example.kt`                                       |
| `{className}`    | `example.ExampleKt`                                |
| `{packageName}`  | `example`                                          |
| `{methodName}`   | `main`                                             |
| `{lineNumber}`   | `6`                                                |
| `{columnNumber}` | `5`                                                |
