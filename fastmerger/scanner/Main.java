package top.fifthlight.fastmerger.scanner;

import io.github.classgraph.ClassGraph;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        var path = Path.of(args[0]);
        try (var scanResult = new ClassGraph()
                .enableInterClassDependencies()
                .overrideClasspath(path.toUri())
                .scan()) {
            var map = scanResult.getClassDependencyMap();
            for (var entry : map.entrySet()) {
                var classInfo = entry.getKey();
                System.out.print(classInfo.getName());
                System.out.println(':');

                for (var classes : entry.getValue()) {
                    System.out.print("    ");
                    System.out.println(classes.getName());
                }
            }
        }
    }
}
