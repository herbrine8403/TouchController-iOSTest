package top.fifthlight.fastmerger.bindeps;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class BindepsWriter implements AutoCloseable {
    private static final int BUFFER_SIZE = 256 * 1024;
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private final FileChannel channel;

    public BindepsWriter(Path path, int stringPoolSize, int classInfoSize) throws IOException {
        channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(BindepsConstraints.MAGIC);
        buffer.putLong(BindepsConstraints.VERSION);
        buffer.putInt(stringPoolSize);
        buffer.putInt(classInfoSize);
    }

    private void flushIfNeeded(int size) throws IOException {
        if (buffer.remaining() >= size) {
            return;
        }
        flush();
    }

    private void flush() throws IOException {
        if (buffer.position() <= 0) {
            return;
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        buffer.clear();
    }

    private void putIntArray(int[] array) {
        buffer.asIntBuffer().put(array);
        buffer.position(buffer.position() + array.length * 4);
    }

    private enum State {
        STRING_POOL, CLASS_INFO,
    }

    private State state = State.STRING_POOL;

    /**
     * Write a string pool entry to file.
     *
     * @param hash        XXHash of the entire name. For example, if this entry represents org.example, "org.example" should be
     *                    hashed, rather than "example"
     * @param parentIndex The index of parent node. If this is the root node, parentIndex should be -1.
     * @param stringBytes The content of this node
     */
    public void writeStringPoolEntry(long hash, int parentIndex, byte[] stringBytes) throws IOException {
        if (state != State.STRING_POOL) {
            throw new IllegalStateException("Bad state: Trying to write string pool when state is " + state);
        }
        if (parentIndex < -1) {
            throw new IllegalStateException("Bad parentIndex: " + parentIndex);
        }
        if (stringBytes.length > Short.MAX_VALUE) {
            throw new IllegalArgumentException("String length exceeds maximum allowed: " + stringBytes.length + ", maximum allowed: " + Short.MAX_VALUE);
        }

        /*
        struct StringPoolEntry {
            uint64_t hash;
            int32_t parentIndex;
            uint16_t length;
            uint8_t content[length];
        */
        var length = 8 + 4 + 2 + stringBytes.length;
        flushIfNeeded(length);

        buffer.putLong(hash);
        buffer.putInt(parentIndex);
        buffer.putShort((short) stringBytes.length);
        buffer.put(stringBytes);
    }

    public void startClassInfo() {
        if (state != State.STRING_POOL) {
            throw new IllegalStateException("Bad state: Trying to start writing class info when state is " + state);
        }
        state = State.CLASS_INFO;
    }

    public void writeClassInfoEntry(int nameIndex, int superIndex, int access, int[] interfaces, int[] annotations, int[] dependencies) throws IOException {
        if (state != State.CLASS_INFO) {
            throw new IllegalStateException("Bad state: Trying to write class info when state is " + state);
        }
        if (nameIndex < 0) {
            throw new IllegalStateException("Bad nameIndex: " + nameIndex);
        }
        if (superIndex < 0) {
            throw new IllegalStateException("Bad superIndex: " + superIndex);
        }

        /*
        struct ClassInfoEntry {
            int32_t nameIndex;
            int32_t superIndex;
            int32_t accessFlag;
            int32_t interfaceLength;
            int32_t interfaces[];
            int32_t annotationLength;
            int32_t annotations[];
            int32_t dependencyLength;
            int32_t dependencies[];
        }
        */
        var length = 4 + 4 + 4 + 4 + interfaces.length * 4 + 4 + annotations.length * 4 + 4 + dependencies.length * 4;
        flushIfNeeded(length);

        buffer.putInt(nameIndex);
        buffer.putInt(superIndex);
        buffer.putInt(access);
        buffer.putInt(interfaces.length);
        putIntArray(interfaces);
        buffer.putInt(annotations.length);
        putIntArray(annotations);
        buffer.putInt(dependencies.length);
        putIntArray(dependencies);
    }

    @Override
    public void close() throws IOException {
        flush();
        channel.close();
    }
}
