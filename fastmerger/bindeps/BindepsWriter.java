package top.fifthlight.fastmerger.bindeps;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class BindepsWriter implements AutoCloseable {
    private static final int BUFFER_SIZE = 256 * 1024;
    private final ByteBuffer indexBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private final ByteBuffer heapBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    private final FileChannel indexChannel;
    private final FileChannel heapChannel;

    private int currentHeapOffset = 0;

    public BindepsWriter(Path indexPath, Path heapPath, int stringPoolSize, int classInfoSize) throws IOException {
        this.indexChannel = FileChannel.open(indexPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        this.heapChannel = FileChannel.open(heapPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

        indexBuffer.order(ByteOrder.BIG_ENDIAN);
        heapBuffer.order(ByteOrder.BIG_ENDIAN);

        indexBuffer.put(BindepsConstraints.MAGIC);
        indexBuffer.putInt(BindepsConstraints.VERSION);
        indexBuffer.putInt(stringPoolSize);
        indexBuffer.putInt(classInfoSize);
    }

    private void flushIfNeeded(FileChannel channel, ByteBuffer buf, int size) throws IOException {
        if (buf.remaining() < size) {
            flush(channel, buf);
        }
    }

    private void flush(FileChannel channel, ByteBuffer buf) throws IOException {
        buf.flip();
        while (buf.hasRemaining()) {
            channel.write(buf);
        }
        buf.clear();
    }

    public void writeStringPoolEntry(long hash, int parentIndex, byte[] stringBytes) throws IOException {
        var len = stringBytes.length;

        // Write index
        flushIfNeeded(indexChannel, indexBuffer, 24);
        indexBuffer.putLong(hash);
        indexBuffer.putInt(parentIndex);
        indexBuffer.putInt(currentHeapOffset);
        indexBuffer.putShort((short) len);
        indexBuffer.put(new byte[6]); // Padded to 24 bytes

        // Write heap
        flushIfNeeded(heapChannel, heapBuffer, len);
        heapBuffer.put(stringBytes);

        currentHeapOffset += len;
    }

    public void writeClassInfoEntry(int nameIndex, int superIndex, int access,
                                    int[] interfaces, int[] annotations, int[] dependencies) throws IOException {
        // Write heap
        var interfaceOffset = writeIntArrayToHeap(interfaces);
        var annotationOffset = writeIntArrayToHeap(annotations);
        var dependenciesOffset = writeIntArrayToHeap(dependencies);

        // Write index
        flushIfNeeded(indexChannel, indexBuffer, 36);
        indexBuffer.putInt(nameIndex);
        indexBuffer.putInt(superIndex);
        indexBuffer.putInt(access);

        indexBuffer.putInt(interfaceOffset);
        indexBuffer.putInt(interfaces.length);

        indexBuffer.putInt(annotationOffset);
        indexBuffer.putInt(annotations.length);

        indexBuffer.putInt(dependenciesOffset);
        indexBuffer.putInt(dependencies.length);
    }

    private int writeIntArrayToHeap(int[] array) throws IOException {
        if (array.length == 0) return -1;

        var startOffset = currentHeapOffset;
        var byteLen = array.length * 4;

        flushIfNeeded(heapChannel, heapBuffer, byteLen);
        for (var i : array) {
            heapBuffer.putInt(i);
        }

        currentHeapOffset += byteLen;
        return startOffset;
    }

    @Override
    public void close() throws IOException {
        flush(indexChannel, indexBuffer);
        flush(heapChannel, heapBuffer);
        indexChannel.close();
        heapChannel.close();
    }
}