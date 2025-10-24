package li.songe.loc

/**
 * Marks a function parameter to be automatically filled with the location of the call site.
 *
 * ```kotlin
 * // use default template
 * @Loc // annotate function for optimize speed compilation
 * fun test1(message: String, @Loc loc: String = "") {
 *     println("[$loc] $message")
 * }
 * test1("Hello, World!")
 * // [example.ExampleKt.main(Example.kt:9)]: Hello, World!
 *
 * // custom template
 * @Loc
 * fun test2(message: String, @Loc("{fileName}:{lineNumber}") loc: String = "") {
 *     println("[$loc] $message")
 * }
 * test2("Hello, World!")
 * // [Example.kt:9]: Hello, World!
 * ```
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.BINARY) // use BINARY to support incremental compile
public annotation class Loc(val value: String = "") // use value save template to support incremental compile
