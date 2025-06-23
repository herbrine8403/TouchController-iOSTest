package top.fifthlight.combine.input.input

interface InputHandler {
    fun updateInputState(textInputState: TextInputState)
    fun tryShowKeyboard()
    fun tryHideKeyboard()
}
