package top.fifthlight.touchcontroller.common.ui.state

import kotlinx.collections.immutable.PersistentList
import top.fifthlight.touchcontroller.assets.TextureSet
import top.fifthlight.touchcontroller.common.control.ControllerWidget

data class WidgetsTabState(
    val listContent: ListContent = ListContent(),
    val tabState: TabState = TabState(),
) {
    data class TabState(
        val listState: ListState = ListState.BUILTIN,
        val dialogState: DialogState = DialogState.Empty,
        val newWidgetParams: NewWidgetParams = NewWidgetParams(),
    )

    data class NewWidgetParams(
        val opacity: Float = .6f,
        val textureSet: TextureSet.TextureSetKey = TextureSet.TextureSetKey.CLASSIC,
    )

    sealed class DialogState {
        data object Empty : DialogState()

        data class ChangeNewWidgetParams(
            val opacity: Float = .6f,
            val textureSet: TextureSet.TextureSetKey = TextureSet.TextureSetKey.CLASSIC,
        ) : DialogState() {
            constructor(params: NewWidgetParams) : this(opacity = params.opacity, textureSet = params.textureSet)

            fun toParams() = NewWidgetParams(
                opacity = opacity,
                textureSet = textureSet,
            )
        }
    }

    enum class ListState {
        BUILTIN,
        CUSTOM
    }

    data class ListContent(
        val heroes: PersistentList<ControllerWidget>? = null,
        val widgets: PersistentList<ControllerWidget>? = null,
    )
}