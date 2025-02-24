package top.fifthlight.touchcontroller

import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.GameOptions
import net.minecraft.client.option.SimpleOption
import top.fifthlight.touchcontroller.config.GameConfigEditor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.reflect.KProperty

object GameConfigEditorImpl : GameConfigEditor {
    private val pendingCallbackLock = ReentrantLock()
    private var pendingCallbacks: MutableList<GameConfigEditor.Callback>? = mutableListOf()

    operator fun <T> SimpleOption<T>.getValue(thisRef: Any?, property: KProperty<*>): T = this.value
    operator fun <T> SimpleOption<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    private class EditorImpl(val options: GameOptions) : GameConfigEditor.Editor {
        override var autoJump: Boolean by options.autoJump
    }

    fun executePendingCallback() {
        pendingCallbackLock.withLock {
            val callbacks = pendingCallbacks
            if (callbacks == null) {
                return
            }
            pendingCallbacks = null
            with(EditorImpl(MinecraftClient.getInstance().options)) {
                callbacks.forEach { callback ->
                    callback.invoke(this)
                }
                options.write()
            }
        }
    }

    override fun submit(callback: GameConfigEditor.Callback) {
        pendingCallbackLock.withLock {
            pendingCallbacks?.add(callback) ?: run {
                with(EditorImpl(MinecraftClient.getInstance().options)) {
                    callback.invoke(this)
                    options.write()
                }
            }
        }
    }
}
