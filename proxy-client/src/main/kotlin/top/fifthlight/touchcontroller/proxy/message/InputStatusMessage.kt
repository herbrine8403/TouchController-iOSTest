package top.fifthlight.touchcontroller.proxy.message

import top.fifthlight.touchcontroller.proxy.message.input.TextInputState
import top.fifthlight.touchcontroller.proxy.message.input.TextRange
import java.nio.ByteBuffer

data class InputStatusMessage(
    val status: TextInputState?,
): ProxyMessage() {
    override val type: Int = 7

    override val wrapInLargeMessage: Boolean
        get() = true

    override fun encode(buffer: ByteBuffer) {
        super.encode(buffer)
        if (status == null) {
            buffer.put(0)
        } else {
            buffer.put(1) // 1
            buffer.putInt(status.text.length) // 5
            buffer.put(status.text.encodeToByteArray()) // 5 + len
            buffer.putInt(status.composition.start) // 9 + len
            buffer.putInt(status.composition.length) // 13 + len
            buffer.putInt(status.selection.start) // 17 + len
            buffer.putInt(status.selection.length) // 21 + len
            buffer.put(if (status.selectionLeft) 1 else 0) // 22 + len
        }
    }

    object Decoder : ProxyMessageDecoder<InputStatusMessage>() {
        override fun decode(payload: ByteBuffer): InputStatusMessage {
            if (payload.remaining() < 22) {
                throw BadMessageLengthException(
                    expected = 22,
                    actual = payload.remaining()
                )
            }

            val hasData = payload.get() != 0.toByte()
            if (!hasData) {
                return InputStatusMessage(null)
            }

            val textLength = payload.getInt()
            if (payload.remaining() < textLength + 17) {
                throw BadMessageLengthException(
                    expected = textLength + 17,
                    actual = payload.remaining()
                )
            }
            val textArray = ByteArray(textLength)
            payload.get(textArray)
            val text = textArray.decodeToString()
            val compositionStart = payload.getInt()
            val compositionLength = payload.getInt()
            val selectionStart = payload.getInt()
            val selectionLength = payload.getInt()
            val selectionLeft = payload.get() != 0.toByte()
            return InputStatusMessage(
                status = TextInputState(
                    text = text,
                    composition = TextRange(
                        start = compositionStart,
                        length = compositionLength
                    ),
                    selection = TextRange(
                        start = selectionStart,
                        length = selectionLength
                    ),
                    selectionLeft = selectionLeft
                )
            )
        }
    }
}