package top.fifthlight.fabazel.mappingmerger.operation;

import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import top.fifthlight.fabazel.mappingmerger.context.MergeContext;

public class ChangeSourceNamespaceOperation implements Operation {
    private final String namespace;

    public ChangeSourceNamespaceOperation(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public MemoryMappingTree run(MemoryMappingTree tree, MergeContext context) throws Exception {
        var newTree = new MemoryMappingTree();
        var visitor = new MappingSourceNsSwitch(newTree, namespace);
        tree.accept(visitor);
        return newTree;
    }
}
