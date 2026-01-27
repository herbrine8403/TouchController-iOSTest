BdepsInfo = provider(fields = {
    "bdeps": "depset of scanned .bdeps files",
})

_DEPS_ATTRS = [
    "deps",
    "runtime_deps",
    "merge_deps",
    "merge_only_deps",
    "merge_runtime_deps",
]

def _bdeps_scan_impl(target, ctx):
    transitive_bdeps = []

    if hasattr(ctx.rule.attr, "deps"):
        for dep in ctx.rule.attr.deps:
            if BdepsInfo in dep:
                transitive_bdeps.append(dep[BdepsInfo].bdeps)

    if hasattr(ctx.rule.attr, "runtime_deps"):
        for dep in ctx.rule.attr.runtime_deps:
            if BdepsInfo in dep:
                transitive_bdeps.append(dep[BdepsInfo].bdeps)

    if hasattr(ctx.rule.attr, "merge_only_deps"):
        for dep in ctx.rule.attr.merge_only_deps:
            if BdepsInfo in dep:
                transitive_bdeps.append(dep[BdepsInfo].bdeps)

    current_bdeps = []
    if JavaInfo in target:
        for jar in target[JavaInfo].runtime_output_jars:
            out_bdeps = ctx.actions.declare_file(jar.basename + ".bdeps")

            args = ctx.actions.args()

            args.add(jar)
            args.add(out_bdeps)

            args.use_param_file("@%s", use_always = True)
            args.set_param_file_format("multiline")

            ctx.actions.run(
                executable = ctx.executable._extractor,
                arguments = [args],
                inputs = [jar],
                outputs = [out_bdeps],
                mnemonic = "ScanBdeps",
                execution_requirements = {
                    "supports-workers": "1",
                    "supports-multiplex-workers": "1",
                    "supports-multiplex-sandboxing": "1",
                    "requires-worker-protocol": "proto",
                },
            )
            current_bdeps.append(out_bdeps)

    res_depset = depset(direct = current_bdeps, transitive = transitive_bdeps)
    return [
        BdepsInfo(bdeps = res_depset),
        OutputGroupInfo(bdeps = res_depset),
    ]

bdeps_scan = aspect(
    implementation = _bdeps_scan_impl,
    attr_aspects = _DEPS_ATTRS,
    attrs = {
        "_extractor": attr.label(
            default = Label("//fastmerger/scanner"),
            executable = True,
            cfg = "exec",
        ),
    },
)
