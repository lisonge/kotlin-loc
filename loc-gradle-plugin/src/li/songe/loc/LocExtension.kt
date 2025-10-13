package li.songe.loc

open class LocExtension {
    internal var projectPath: String = ""

    var template: String = "{className}.{methodName}({fileName}:{lineNumber})"

    internal val entries
        get() = listOf(
            "projectPath" to projectPath,
            "template" to template,
        )
}
