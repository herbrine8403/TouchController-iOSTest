package top.fifthlight.armorstand;

import net.fabricmc.accesswidener.AccessWidenerReader;
import net.fabricmc.accesswidener.AccessWidenerVisitor;
import top.fifthlight.bazel.worker.api.Worker;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class AccessWidenerConvertor extends Worker {
    private record AccessTransformerWriter(Writer writer) implements AccessWidenerVisitor, AutoCloseable {
        @Override
        public void visitClass(String name, AccessWidenerReader.AccessType access, boolean transitive) {
            try {
                switch (access) {
                    case ACCESSIBLE -> writer.write("public");
                    case EXTENDABLE -> writer.write("public-f");
                    case MUTABLE -> throw new IllegalArgumentException("Bad access for class: " + access);
                }
                writer.write(' ');
                writer.write(name.replace('/', '.'));
                writer.write('\n');
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void visitMethod(String owner, String name, String descriptor, AccessWidenerReader.AccessType access, boolean transitive) {
            try {
                switch (access) {
                    case ACCESSIBLE -> writer.write("public");
                    case EXTENDABLE -> writer.write("public-f");
                    case MUTABLE -> throw new IllegalArgumentException("Bad access for method: " + access);
                }
                writer.write(' ');
                writer.write(owner.replace('/', '.'));
                writer.write(' ');
                writer.write(name);
                writer.write(descriptor);
                writer.write('\n');
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void visitField(String owner, String name, String descriptor, AccessWidenerReader.AccessType access, boolean transitive) {
            try {
                switch (access) {
                    case ACCESSIBLE -> writer.write("public");
                    case EXTENDABLE -> throw new IllegalArgumentException("Bad access for field: " + access);
                    case MUTABLE -> writer.write("public-f");
                }
                writer.write(' ');
                writer.write(owner.replace('/', '.'));
                writer.write(' ');
                writer.write(name);
                writer.write('\n');
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() throws IOException {
            writer.close();
        }
    }

    public static void main(String[] args) throws Exception {
        new AccessWidenerConvertor().run(args);
    }

    @Override
    protected int handleRequest(PrintWriter out, Path sandboxDir, String... args) {
        try {
            if (args.length < 2) {
                out.println("Usage: AccessWidenerConvertor <input> <output>");
                return 1;
            }

            var inputFile = sandboxDir.resolve(Path.of(args[0]));
            var outputFile = sandboxDir.resolve(Path.of(args[1]));

            try (var writer = Files.newBufferedWriter(outputFile); var accessTransformerWriter = new AccessTransformerWriter(writer)) {
                var accessWidenerReader = new AccessWidenerReader(accessTransformerWriter);
                try (var reader = Files.newBufferedReader(inputFile)) {
                    accessWidenerReader.read(reader);
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace(out);
            return 1;
        }
    }
}