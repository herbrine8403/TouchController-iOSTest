package top.fifthlight.touchcontroller.common.input

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.input.input.InputHandler
import top.fifthlight.combine.input.input.TextInputState
import top.fifthlight.touchcontroller.common.event.RenderEvents
import top.fifthlight.touchcontroller.common.platform.PlatformProvider
import top.fifthlight.touchcontroller.proxy.message.InputStatusMessage
import top.fifthlight.touchcontroller.proxy.message.KeyboardShowMessage
import top.fifthlight.touchcontroller.proxy.message.input.TextInputState as ProxyTextInputState
import top.fifthlight.touchcontroller.proxy.message.input.TextRange as ProxyTextRange

object InputManager: KoinComponent, InputHandler {
    private val platformProvider: PlatformProvider by inject()
    private var inputState: TextInputState? = null

    override fun updateInputState(textInputState: TextInputState) {
        inputState = textInputState
        platformProvider.platform?.let { platform ->
            if (RenderEvents.platformCapabilities.textStatus) {
                platform.sendEvent(InputStatusMessage(ProxyTextInputState(
                    text = textInputState.text,
                    composition = ProxyTextRange(
                        start = textInputState.composition.start,
                        length = textInputState.composition.length,
                    ),
                    selection = ProxyTextRange(
                        start = textInputState.selection.start,
                        length = textInputState.selection.length,
                    ),
                    selectionLeft = textInputState.selectionLeft,
                )))
            }
        }
    }

    override fun tryShowKeyboard() {
        platformProvider.platform?.let { platform ->
            if (RenderEvents.platformCapabilities.keyboardShow) {
                platform.sendEvent(KeyboardShowMessage(true))
            }
        }
    }

    override fun tryHideKeyboard() {
        platformProvider.platform?.let { platform ->
            if (RenderEvents.platformCapabilities.keyboardShow) {
                platform.sendEvent(KeyboardShowMessage(false))
            }
        }
    }
}
