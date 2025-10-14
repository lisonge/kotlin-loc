package li.songe.loc

/**
 * A string constant that is automatically filled with the location of the call site.
 *
 * ```kotlin
 * println("[${LOC}]: Hello, World!")
 * // [example.ExampleKt.main(Example.kt:6)]: Hello, World!
 * ```
 */
public val LOC: String get() = throw NotImplementedError()

/**
 * Marks a function parameter to be automatically filled with the location of the call site.
 *
 * ```kotlin
 * // use default template
 * fun test1(message: String, @Loc loc: String = "") {
 *     println("[$loc] $message")
 * }
 * test1("Hello, World!")
 * // [example.ExampleKt.main(Example.kt:9)]: Hello, World!
 *
 * // custom template
 * fun test2(message: String, @Loc loc: String = "{fileName}:{lineNumber}") {
 *     println("[$loc] $message")
 * }
 * test2("Hello, World!")
 * // [Example.kt:9]: Hello, World!
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
public annotation class Loc
