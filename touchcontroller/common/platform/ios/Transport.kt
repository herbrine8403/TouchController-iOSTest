package top.fifthlight.touchcontroller.common.platform.ios

object Transport {
    private external fun init()
    external fun new(path: String): Long
    external fun receive(handle: Long, buffer: ByteArray): Int
    external fun send(handle: Long, buffer: ByteArray, off: Int, len: Int)
    external fun destroy(handle: Long)

    init {
        // TODO: deal with NeoForge
        init()
    }
}