package top.fifthlight.touchcontroller.common.layout.widget.button

data class ButtonResult(
    val newPointer: Boolean = false,
    val clicked: Boolean = false,
    val release: Boolean = false,
)
