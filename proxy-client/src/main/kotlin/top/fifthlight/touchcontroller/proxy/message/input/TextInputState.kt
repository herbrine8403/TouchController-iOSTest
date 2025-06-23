package top.fifthlight.touchcontroller.proxy.message.input

data class TextInputState(
    val text: String = "",
    val composition: TextRange = TextRange.EMPTY,
    val selection: TextRange = TextRange(text.length),
    val selectionLeft: Boolean = true,
) {
    init {
        require(composition.end <= text.length) { "composition region end ${composition.end} should not exceed text length ${text.length}" }
        require(selection.end <= text.length) { "selection region end ${selection.end} should not exceed text length ${text.length}" }
    }

    val compositionText = text.substring(composition)
    val selectionText = text.substring(selection)
}

