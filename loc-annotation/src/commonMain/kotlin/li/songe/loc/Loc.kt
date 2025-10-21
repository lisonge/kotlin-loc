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
 * fun test2(message: String, @Loc loc: String = "{fileName}:{lineNumber}") {
 *     println("[$loc] $message")
 * }
 * test2("Hello, World!")
 * // [Example.kt:9]: Hello, World!
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
public annotation class Loc
