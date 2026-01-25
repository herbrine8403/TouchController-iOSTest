package top.fifthlight.fastmerger.scanner.classdeps;

import java.util.ArrayList;
import java.util.HashSet;

public class ClassInfoCollector implements ClassInfoVisitor.Consumer {
    private final ClassNameMap classNameMap;
    private ClassNameMap.Entry entry;
    private int accessFlag;
    private ClassNameMap.Entry superClass;
    private ArrayList<ClassNameMap.Entry> interfaces = new ArrayList<>();
    private HashSet<ClassNameMap.Entry> annotations = new HashSet<>();
    private HashSet<ClassNameMap.Entry> dependencies = new HashSet<>(16);

    public ClassInfoCollector(ClassNameMap classNameMap) {
        this.classNameMap = classNameMap;
    }

    @Override
    public void acceptClassInfo(String className, int accessFlag, String superClass) {
        entry = classNameMap.getOrCreate(className);
        this.accessFlag = accessFlag;
        this.superClass = classNameMap.getOrCreate(superClass);
    }

    @Override
    public void acceptInterface(String interfaceName) {
        interfaces.add(classNameMap.getOrCreate(interfaceName));
    }

    @Override
    public void acceptAnnotation(String annotationName) {
        annotations.add(classNameMap.getOrCreate(annotationName));
    }

    @Override
    public void acceptClassDependency(String dependencyName) {
        dependencies.add(classNameMap.getOrCreate(dependencyName));
    }

    public ClassInfo getClassInfo() {
        return new ClassInfo(
                classNameMap, entry, accessFlag, superClass,
                interfaces.toArray(new ClassNameMap.Entry[0]),
                annotations.toArray(new ClassNameMap.Entry[0]),
                dependencies.toArray(new ClassNameMap.Entry[0])
        );
    }
}
