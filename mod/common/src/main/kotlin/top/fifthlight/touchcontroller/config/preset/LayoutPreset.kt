package top.fifthlight.touchcontroller.config.preset

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.fifthlight.data.IntOffset
import top.fifthlight.touchcontroller.config.*
import top.fifthlight.touchcontroller.control.*
import top.fifthlight.touchcontroller.ext.LayoutPresetsSerializer
import top.fifthlight.touchcontroller.layout.Align

@Immutable
@Serializable
data class LayoutPreset(
    val name: String,
    val controlInfo: PresetControlInfo = PresetControlInfo(),
    val layout: ControllerLayout = ControllerLayout(),
) {
    companion object {
        const val DEFAULT_LAYOUT_NAME = "Empty layout"
    }
}

@Immutable
@Serializable
data class PresetControlInfo(
    val splitControls: Boolean = false,
    val disableTouchGesture: Boolean = false,
)

@JvmInline
@Serializable(with = LayoutPresetsSerializer::class)
value class LayoutPresets(
    val presets: PersistentList<LayoutPreset> = persistentListOf(),
)

fun layoutPresetsOf(vararg pairs: LayoutPreset) = LayoutPresets(persistentListOf(*pairs))
