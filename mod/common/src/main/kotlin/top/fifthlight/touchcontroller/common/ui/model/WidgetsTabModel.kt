package top.fifthlight.touchcontroller.common.ui.model

import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.common.config.widget.WidgetPresetManager
import top.fifthlight.touchcontroller.common.control.*
import top.fifthlight.touchcontroller.common.ext.combineStates
import top.fifthlight.touchcontroller.common.ui.state.WidgetsTabState
import top.fifthlight.touchcontroller.common.ui.state.WidgetsTabState.ListContent

private val builtInWidgets = ListContent(
    heroes = persistentListOf<ControllerWidget>(
        DPad(),
        Joystick()
    ),
    widgets = persistentListOf<ControllerWidget>(
        AscendButton(),
        DescendButton(),
        BoatButton(),
        ChatButton(),
        DescendButton(),
        ForwardButton(),
        HideHudButton(),
        InventoryButton(),
        JumpButton(),
        PanoramaButton(),
        PauseButton(),
        PerspectiveSwitchButton(),
        PlayerListButton(),
        ScreenshotButton(),
        SneakButton(),
        SprintButton(),
        UseButton(),
        CustomWidget(),
    )
)

class WidgetsTabModel(
    private val screenModel: CustomControlLayoutTabModel
) : TouchControllerScreenModel() {
    private val widgetPresetManager: WidgetPresetManager by inject()
    private val tabState = MutableStateFlow(WidgetsTabState.TabState())
    val uiState = combineStates(tabState, widgetPresetManager.presets) { tabState, presets ->
        WidgetsTabState(
            listContent = when (tabState.listState) {
                WidgetsTabState.ListState.BUILTIN -> builtInWidgets
                WidgetsTabState.ListState.CUSTOM -> ListContent(widgets = presets)
            },
            tabState = tabState,
        )
    }

    fun selectBuiltinTab() {
        tabState.getAndUpdate {
            it.copy(listState = WidgetsTabState.ListState.BUILTIN)
        }
    }

    fun selectCustomTab() {
        tabState.getAndUpdate {
            it.copy(listState = WidgetsTabState.ListState.CUSTOM)
        }
    }

    fun openNewWidgetParamsDialog() {
        tabState.getAndUpdate {
            it.copy(dialogState = WidgetsTabState.DialogState.ChangeNewWidgetParams(it.newWidgetParams))
        }
    }

    fun updateNewWidgetParamsDialog(editor: WidgetsTabState.DialogState.ChangeNewWidgetParams.() -> WidgetsTabState.DialogState.ChangeNewWidgetParams) {
        tabState.getAndUpdate {
            var params = it.dialogState
            if (params is WidgetsTabState.DialogState.ChangeNewWidgetParams) {
                params = editor(params)
            }
            it.copy(dialogState = params)
        }
    }

    fun closeDialog() {
        tabState.getAndUpdate {
            it.copy(dialogState = WidgetsTabState.DialogState.Empty)
        }
    }

    fun updateNewWidgetParams(params: WidgetsTabState.NewWidgetParams) {
        tabState.getAndUpdate {
            it.copy(newWidgetParams = params)
        }
    }
}