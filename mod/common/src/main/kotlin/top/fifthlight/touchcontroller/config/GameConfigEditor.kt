package top.fifthlight.touchcontroller.config

interface GameConfigEditor {
    interface Editor {
        var autoJump: Boolean
    }

    fun interface Callback {
        fun invoke(editor: Editor)
    }

    fun submit(callback: Callback)
}
