"""Rules for publishing artifacts to Maven repositories."""

load("@rules_java//java/common:java_info.bzl", "JavaInfo")
load("//rule:merge_jar.bzl", "merge_jar_action")

_SH_TOOLCHAIN_TYPE = "@rules_shell//shell:toolchain_type"

def _maven_publish_impl(ctx):
    parts = ctx.attr.coordinate.split(":")
    if len(parts) != 3:
        fail("coordinate must be in format 'groupId:artifactId:version'")
    groupId, artifactId, version = parts

    artifact_specs = []
    input_files = []

    java_info = ctx.attr.src[JavaInfo]
    main_jars_depset = java_info.full_compile_jars
    main_jar = ctx.actions.declare_file(ctx.label.name + "_classes.jar")
    merge_jar_action(
        ctx.actions,
        ctx.executable._merge_jar_executable,
        main_jar,
        main_jars_depset,
    )
    artifact_specs.append(struct(
        id = ":jar",
        path = main_jar.short_path,
    ))
    input_files.append(main_jar)

    source_jars = java_info.source_jars
    source_jar = None
    if source_jars:
        source_jar = ctx.actions.declare_file(ctx.label.name + "_sources.jar")
        merge_jar_action(
            ctx.actions,
            ctx.executable._merge_jar_executable,
            source_jar,
            depset(source_jars),
        )
        artifact_specs.append(struct(
            id = "sources:jar",
            path = source_jar.short_path,
        ))
        input_files.append(source_jar)

    for classifier_ext, target in ctx.attr.artifacts.items():
        # Parse key: "classifier[:extension]"
        if ":" in classifier_ext:
            classifier, explicit_ext = classifier_ext.split(":", 1)
        else:
            classifier = classifier_ext
            explicit_ext = ""

        file_list = target[DefaultInfo].files.to_list()
        if not file_list:
            fail("No files found for artifact: " + classifier_ext)
        if len(file_list) != 1:
            fail("Expected exactly one file for artifact: " + classifier_ext + ", got " + str(len(file_list)))
        file = file_list[0]
        input_files.append(file)

        if not explicit_ext:
            extension = file.extension

        if extension == "pom":
            fail("Cannot override POM artifact. Use pom_template attribute instead.")

        artifact_specs.append(struct(
            id = classifier + ":" + extension,
            path = file.short_path,
        ))

    substitutions = {
        "{groupId}": groupId,
        "{artifactId}": artifactId,
        "{version}": version,
    }
    substitutions.update(ctx.attr.pom_substitutions)

    pom_template = ctx.file.pom_template if ctx.attr.pom_template else ctx.file._default_pom_template
    output_pom = ctx.actions.declare_file(ctx.label.name + ".pom")
    ctx.actions.expand_template(
        output = output_pom,
        template = pom_template,
        substitutions = substitutions,
    )

    wrapper_substitutions = {
        "{WORKSPACE_NAME}": ctx.workspace_name,
        "{EXEC_PATH}": ctx.executable._maven_publisher_binary.short_path,
        "{POM_PATH}": output_pom.short_path,
        "{GROUP_ID}": groupId,
        "{ARTIFACT_ID}": artifactId,
        "{VERSION}": version,
        "{ARTIFACT_IDS}": ' '.join([("'%s'" % spec.id) for spec in artifact_specs]),
        "{ARTIFACT_PATHS}": ' '.join([("'%s'" % spec.path) for spec in artifact_specs]),
    }

    if ctx.target_platform_has_constraint(ctx.attr._windows_constraint[platform_common.ConstraintValueInfo]):
        output_executable = ctx.actions.declare_file(ctx.attr.name + ".bat")
        output_script = ctx.actions.declare_file(ctx.attr.name + ".bash")
        ctx.actions.expand_template(
            output = output_script,
            template = ctx.file._wrapper_template,
            substitutions = wrapper_substitutions,
            is_executable = True,
        )

        sh_toolchain = ctx.toolchains[_SH_TOOLCHAIN_TYPE]
        if not sh_toolchain or not sh_toolchain.path:
            fail("No suitable shell toolchain found")

        runfiles = ctx.runfiles(
            files = [
                ctx.file._rlocation_library,
                output_pom,
                main_jar,
                output_script,
            ] + input_files,
        ).merge(
            ctx.attr._maven_publisher_binary[DefaultInfo].default_runfiles,
        )

        ctx.actions.write(
            output = output_executable,
            content = sh_toolchain.path + " -c 'PATH=/usr/local/bin:/usr/bin:/bin:/opt/bin:$PATH RUNFILES_MANIFEST_FILE=../%s.bat.runfiles_manifest ../" % ctx.attr.name + output_script.basename + "'",
            is_executable = True,
        )
    else:
        output_executable = ctx.actions.declare_file(ctx.attr.name + ".bash")
        runfiles = ctx.runfiles(
            files = [
                ctx.file._rlocation_library,
                output_pom,
                main_jar,
            ] + input_files,
        ).merge(
            ctx.attr._maven_publisher_binary[DefaultInfo].default_runfiles,
        )

        ctx.actions.expand_template(
            output = output_executable,
            template = ctx.file._wrapper_template,
            substitutions = wrapper_substitutions,
            is_executable = True,
        )

    return [DefaultInfo(
        runfiles = runfiles,
        executable = output_executable,
    )]

maven_publish = rule(
    implementation = _maven_publish_impl,
    executable = True,
    toolchains = [
        config_common.toolchain_type(_SH_TOOLCHAIN_TYPE, mandatory = False),
    ],
    attrs = {
        "coordinate": attr.string(
            mandatory = True,
            doc = "Maven coordinate in format 'groupId:artifactId:version'",
        ),
        "src": attr.label(
            mandatory = True,
            providers = [JavaInfo],
            doc = "JavaInfo target providing jar and sources",
        ),
        "artifacts": attr.string_keyed_label_dict(
            mandatory = False,
            default = {},
            doc = "Custom artifacts. Key format: 'classifier[:extension]', Value: label",
        ),
        "pom_template": attr.label(
            mandatory = False,
            allow_single_file = [".xml"],
            doc = "Custom POM template file",
        ),
        "pom_substitutions": attr.string_dict(
            mandatory = False,
            default = {},
            doc = "Template variable substitutions",
        ),
        "_maven_publisher_binary": attr.label(
            default = "//rule/maven_publisher:maven_publisher",
            executable = True,
            cfg = "exec",
        ),
        "_wrapper_template": attr.label(
            default = "//rule/maven_publisher:maven_publisher_wrapper",
            allow_single_file = [".bash"],
        ),
        "_default_pom_template": attr.label(
            default = "//rule/maven_publisher:pom_template",
            allow_single_file = [".xml"],
        ),
        "_rlocation_library": attr.label(
            default = "@bazel_tools//tools/bash/runfiles",
            allow_single_file = [".bash"],
        ),
        "_windows_constraint": attr.label(
            default = "@platforms//os:windows",
        ),
        "_merge_jar_executable": attr.label(
            default = "@//rule/merge_jar",
            executable = True,
            cfg = "exec",
        ),
    },
    doc = "Publishes Java artifacts to a Maven repository",
)
