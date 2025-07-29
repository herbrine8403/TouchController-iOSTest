package top.fifthlight.touchcontroller.common.ui.tab.layout.custom

import androidx.compose.runtime.*
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.koin.core.parameter.parametersOf
import top.fifthlight.combine.animation.animateFloatAsState
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.anchor
import top.fifthlight.combine.modifier.placement.fillMaxSize
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.ui.*
import top.fifthlight.data.IntRect
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.common.config.preset.LayoutPreset
import top.fifthlight.touchcontroller.common.ui.component.ListButton
import top.fifthlight.touchcontroller.common.ui.component.TabButton
import top.fifthlight.touchcontroller.common.ui.component.TwoItemRow
import top.fifthlight.touchcontroller.common.ui.model.PresetsTabModel
import top.fifthlight.touchcontroller.common.ui.state.PresetsTabState
import kotlin.uuid.Uuid

@Composable
private fun PresetsList(
    modifier: Modifier = Modifier,
    listContent: PersistentList<Pair<Uuid, LayoutPreset>> = persistentListOf(),
    currentSelectedPresetUuid: Uuid? = null,
    onPresetSelected: (Uuid, LayoutPreset) -> Unit = { _, _ -> },
    onPresetEdited: (Uuid, LayoutPreset) -> Unit = { _, _ -> },
    onPresetShowPath: (Uuid) -> Unit = {},
    onPresetCopied: (Uuid, LayoutPreset) -> Unit = { _, _ -> },
    onPresetDeleted: (Uuid, LayoutPreset) -> Unit = { _, _ -> },
) {
    for ((uuid, preset) in listContent) {
        TwoItemRow(
            modifier = modifier,
            rightWidth = 24,
            space = 4,
        ) {
            TabButton(
                modifier = Modifier.fillMaxWidth(),
                checked = currentSelectedPresetUuid == uuid,
                onClick = {
                    onPresetSelected(uuid, preset)
                },
            ) {
                Text(preset.name)
            }

            var popupOpened by remember { mutableStateOf(false) }
            var anchor by remember { mutableStateOf(IntRect.ZERO) }
            ListButton(
                modifier = Modifier.anchor { anchor = it },
                onClick = {
                    popupOpened = true
                },
            ) {
                Icon(Textures.ICON_MENU)
            }

            val expandProgress by animateFloatAsState(if (popupOpened) 1f else 0f)
            if (expandProgress != 0f) {
                DropDownMenu(
                    expandProgress = expandProgress,
                    anchor = anchor,
                    onDismissRequest = {
                        popupOpened = false
                    }
                ) {
                    DropdownItemList(
                        modifier = Modifier.verticalScroll(),
                        onItemSelected = { popupOpened = false },
                        items = persistentListOf(
                            Pair(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_EDIT)) {
                                onPresetEdited(uuid, preset)
                            },
                            Pair(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_SHOW_PATH)) {
                                onPresetShowPath(uuid)
                            },
                            Pair(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_COPY)) {
                                onPresetCopied(uuid, preset)
                            },
                            Pair(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_DELETE)) {
                                onPresetDeleted(uuid, preset)
                            },
                        ),
                    )
                }
            }
        }
    }
}

object PresetsTab : CustomTab() {
    @Composable
    override fun Icon() {
        Icon(Textures.ICON_PRESET)
    }

    @Composable
    override fun Content() {
        val (screenModel, uiState, tabsButton, sideBarAtRight) = LocalCustomTabContext.current
        val tabModel: PresetsTabModel = koinScreenModel { parametersOf(screenModel) }
        val tabState by tabModel.uiState.collectAsState()
        val navigator = LocalNavigator.current
        AlertDialog(
            visible = tabState == PresetsTabState.CreateChoose,
            onDismissRequest = { tabModel.clearState() },
            title = {
                Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_CREATE_PRESET_CHOOSE))
            },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(.4f),
                verticalArrangement = Arrangement.spacedBy(4),
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navigator?.parent?.push(ImportPresetScreen { key ->
                            screenModel.newPreset(key.preset)
                        })
                        tabModel.clearState()
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_CREATE_PRESET_CHOOSE_PRESET))
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        tabModel.openCreateEmptyPresetDialog()
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_CREATE_PRESET_CHOOSE_EMPTY))
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        tabModel.clearState()
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_CREATE_PRESET_CHOOSE_CANCEL))
                }
            }
        }
        AlertDialog(
            value = tabState,
            valueTransformer = { tabState as? PresetsTabState.CreateEmpty },
            title = {
                Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_CREATE_EMPTY_PRESET))
            },
            action = { state ->
                GuideButton(
                    onClick = {
                        tabModel.createPreset(state)
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_CREATE_EMPTY_PRESET_CREATE))
                }
                Button(
                    onClick = {
                        tabModel.clearState()
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_CREATE_EMPTY_PRESET_CANCEL))
                }
            }
        ) { state ->
            Column(
                modifier = Modifier.fillMaxWidth(.5f),
                verticalArrangement = Arrangement.spacedBy(4),
            ) {
                EditText(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.name,
                    onValueChanged = {
                        tabModel.updateCreatePresetState { copy(name = it) }
                    },
                    placeholder = Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_NAME_PLACEHOLDER),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_SPLIT_CONTROLS))

                    Switch(
                        value = state.controlInfo.splitControls,
                        onValueChanged = {
                            tabModel.updateCreatePresetState {
                                copy(controlInfo = controlInfo.copy(splitControls = it))
                            }
                        },
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_DISABLE_TOUCH_GESTURES))

                    Switch(
                        value = state.controlInfo.disableTouchGesture,
                        onValueChanged = {
                            tabModel.updateCreatePresetState {
                                copy(controlInfo = controlInfo.copy(disableTouchGesture = it))
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_DISABLE_CROSSHAIR))

                    Switch(
                        value = state.controlInfo.disableCrosshair,
                        onValueChanged = {
                            tabModel.updateCreatePresetState {
                                copy(controlInfo = controlInfo.copy(disableCrosshair = it))
                            }
                        }
                    )
                }
            }
        }
        AlertDialog(
            value = tabState,
            valueTransformer = { tabState as? PresetsTabState.Edit },
            title = {
                Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_EDIT_PRESET))
            },
            action = { state ->
                GuideButton(
                    onClick = {
                        if (uiState.selectedPresetUuid == state.uuid) {
                            screenModel.editPreset(false, state::edit)
                            screenModel.savePreset()
                            tabModel.clearState()
                        } else {
                            tabModel.editPreset(state)
                        }
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_EDIT_PRESET_OK))
                }
                Button(
                    onClick = {
                        tabModel.clearState()
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_EDIT_PRESET_CANCEL))
                }
            }
        ) { state ->
            Column(
                modifier = Modifier.fillMaxWidth(.5f),
                verticalArrangement = Arrangement.spacedBy(4),
            ) {
                EditText(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.name,
                    onValueChanged = {
                        tabModel.updateEditPresetState { copy(name = it) }
                    },
                    placeholder = Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_NAME_PLACEHOLDER),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_SPLIT_CONTROLS))

                    Switch(
                        value = state.controlInfo.splitControls,
                        onValueChanged = {
                            tabModel.updateEditPresetState {
                                copy(controlInfo = controlInfo.copy(splitControls = it))
                            }
                        },
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_DISABLE_TOUCH_GESTURES))

                    Switch(
                        value = state.controlInfo.disableTouchGesture,
                        onValueChanged = {
                            tabModel.updateEditPresetState {
                                copy(controlInfo = controlInfo.copy(disableTouchGesture = it))
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_DISABLE_CROSSHAIR))

                    Switch(
                        value = state.controlInfo.disableCrosshair,
                        onValueChanged = {
                            tabModel.updateCreatePresetState {
                                copy(controlInfo = controlInfo.copy(disableCrosshair = it))
                            }
                        }
                    )
                }
            }
        }
        AlertDialog(
            value = tabState,
            valueTransformer = { tabState as? PresetsTabState.Delete },
            onDismissRequest = { tabModel.clearState() },
            action = { state ->
                WarningButton(
                    onClick = {
                        screenModel.deletePreset(state.uuid)
                        tabModel.clearState()
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_DELETE_PRESET_DELETE))
                }
                Button(
                    onClick = {
                        tabModel.clearState()
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_DELETE_PRESET_CANCEL))
                }
            }
        ) { state ->
            val presetName = uiState.allPresets[state.uuid]?.name ?: "ERROR"
            Column(
                verticalArrangement = Arrangement.spacedBy(4),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_DELETE_PRESET_1))
                Text(Text.format(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_DELETE_PRESET_2, presetName))
            }
        }
        AlertDialog(
            value = tabState,
            valueTransformer = { tabState as? PresetsTabState.Path },
            onDismissRequest = { tabModel.clearState() },
            title = {
                Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_PATH))
            },
            action = { state ->
                GuideButton(
                    onClick = {
                        tabModel.clearState()
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_PATH_OK))
                }
            }
        ) { state ->
            if (state.path != null) {
                Text(Text.literal(state.path))
            } else {
                Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_PATH_GET_FAILED))
            }
        }

        SideBarContainer(
            sideBarAtRight = sideBarAtRight,
            tabsButton = tabsButton,
            actions = {
                IconButton(
                    onClick = {
                        tabModel.openCreatePresetChooseDialog()
                    }
                ) {
                    Icon(Textures.ICON_ADD)
                }
            }
        ) { modifier ->
            SideBarScaffold(
                modifier = modifier,
                title = {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS))
                },
                actions = {
                    val selectedUuid = uiState.selectedPresetUuid
                    val indices = uiState.allPresets.orderedEntries.indices
                    val index = selectedUuid?.let { selectedUuid ->
                        uiState.allPresets.orderedEntries.indexOfFirst { (uuid, _) -> uuid == selectedUuid }
                    } ?: run {
                        -1
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = selectedUuid != null && index > 0,
                        onClick = {
                            selectedUuid?.let { uuid ->
                                tabModel.movePreset(uuid, -1)
                            }
                        }
                    ) {
                        Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_MOVE_UP))
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = selectedUuid != null && index < indices.last,
                        onClick = {
                            selectedUuid?.let { uuid ->
                                tabModel.movePreset(uuid, 1)
                            }
                        }
                    ) {
                        Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_PRESETS_MOVE_DOWN))
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll()
                        .fillMaxSize()
                ) {
                    PresetsList(
                        modifier = Modifier.fillMaxWidth(),
                        listContent = uiState.allPresets.orderedEntries,
                        currentSelectedPresetUuid = uiState.selectedPresetUuid,
                        onPresetSelected = { uuid, preset ->
                            screenModel.selectPreset(uuid)
                        },
                        onPresetEdited = { uuid, preset ->
                            tabModel.openEditPresetDialog(uuid, preset)
                        },
                        onPresetShowPath = { uuid ->
                            tabModel.openPresetPathDialog(uuid)
                        },
                        onPresetCopied = { uuid, preset ->
                            screenModel.newPreset(preset)
                        },
                        onPresetDeleted = { uuid, preset ->
                            tabModel.openDeletePresetBox(uuid)
                        }
                    )
                }
            }
        }
    }
}