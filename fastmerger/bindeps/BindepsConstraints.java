package top.fifthlight.fastmerger.bindeps;

public class BindepsConstraints {
    private BindepsConstraints() {
    }

    public static final byte[] MAGIC = new byte[]{0x42, 0x49, 0x4E, 0x44, 0x45, 0x50, 0x53, 0x03};
    public static final int VERSION = 1;
}
