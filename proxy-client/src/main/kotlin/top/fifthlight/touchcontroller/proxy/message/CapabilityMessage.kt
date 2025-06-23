package top.fifthlight.touchcontroller.proxy.message

import java.nio.ByteBuffer

data class CapabilityMessage (
    val capability: String,
): ProxyMessage() {
    override val type: Int = 5

    init {
        require(capability.length < 128) { "Capability too long: $capability" }
    }

    override fun encode(buffer: ByteBuffer) {
        super.encode(buffer)
        buffer.put(capability.length.toByte())
        buffer.put(capability.encodeToByteArray())
    }

    object Decoder : ProxyMessageDecoder<CapabilityMessage>() {
        override fun decode(payload: ByteBuffer): CapabilityMessage {
            if (payload.remaining() < 1) {
                throw BadMessageLengthException(
                    expected = 1,
                    actual = payload.remaining()
                )
            }
            val length = payload.get().toUInt().toInt()
            if (length <= 0) {
                throw BadMessageException("Bad capability message: length $length")
            }
            if (payload.remaining() != length) {
                throw BadMessageLengthException(
                    expected = length,
                    actual = payload.remaining()
                )
            }
            val byteArray = ByteArray(length)
            payload.get(byteArray)
            return CapabilityMessage(byteArray.decodeToString())
        }
    }
}