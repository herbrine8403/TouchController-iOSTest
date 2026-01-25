package top.fifthlight.fastmerger.scanner.classdeps;

public record ClassInfo(
        ClassNameMap classNameMap,
        ClassNameMap.Entry entry,
        int accessFlag,
        ClassNameMap.Entry superClass,
        ClassNameMap.Entry[] interfaces,
        ClassNameMap.Entry[] annotations,
        ClassNameMap.Entry[] dependencies
) {
    public String getFullName() {
        return entry.fullName();
    }
}
