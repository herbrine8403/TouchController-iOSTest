"""Rules to extract native libraris for Minecraft versions using LWJGL2."""

load("@rules_java//java/common:java_info.bzl", "JavaInfo")

ExtractLibInfo = provider(
    "Provider for JAR to be extracted with exclude patterns",
    fields = ["jar_file", "excludes"],
)

def _extract_lib_impl(ctx):
    return [ExtractLibInfo(
        jar_file = ctx.file.jar,
        excludes = ctx.attr.excludes,
    ), DefaultInfo(
        files = depset([ctx.file.jar]),
    )]

extract_lib = rule(
    doc = "Define a JAR file to be extracted with exclude patterns",
    implementation = _extract_lib_impl,
    attrs = {
        "jar": attr.label(
            doc = "The JAR file to be extracted",
            mandatory = True,
            allow_single_file = ["jar"],
        ),
        "excludes": attr.string_list(
            doc = "List of exclude patterns",
            mandatory = False,
            default = [],
        ),
    },
)
