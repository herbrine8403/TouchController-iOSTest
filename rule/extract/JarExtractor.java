package top.fifthlight.fabazel.jarextractor;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;
import top.fifthlight.bazel.worker.api.Worker;

public class JarExtractor extends Worker {
    public static void main(String[] args) throws Exception {
        new JarExtractor().run(args);
    }

    @Override
    protected int handleRequest(PrintWriter out, Path sandboxDir, String... args) {
        try {
            if (args.length != 3) {
                out.println("Bad count of arguments: " + args.length + ", expected 3");
                return 1;
            }

            var jarPath = sandboxDir.resolve(Paths.get(args[0]));
            var entryPath = args[1];
            var outputPath = sandboxDir.resolve(Paths.get(args[2]));

            try {
                Files.createDirectories(outputPath.getParent());
            } catch (IOException e) {
                out.println("Failed to create output directories: " + e.getMessage());
                e.printStackTrace(out);
                return 1;
            }

            try (var jar = new JarFile(jarPath.toFile())) {
                var entry = jar.getJarEntry(entryPath);
                if (entry == null) {
                    out.println("Entry '" + entryPath + "' not found in JAR: " + jarPath);
                    return 1;
                }

                try (var input = jar.getInputStream(entry);
                     var output = Files.newOutputStream(outputPath)) {
                    input.transferTo(output);
                }
            }

            return 0;
        } catch (Exception ex) {
            ex.printStackTrace(out);
            return 1;
        }
    }
}
