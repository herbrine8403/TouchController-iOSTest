package top.fifthlight.touchcontroller.ui.tab.layout.custom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import cafe.adriel.voyager.koin.koinScreenModel
import org.koin.core.parameter.parametersOf
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.placement.fillMaxSize
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.ColumnScope
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.combine.widget.ui.Icon
import top.fifthlight.combine.widget.ui.IconButton
import top.fifthlight.combine.widget.ui.Text
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.ui.model.CustomControlLayoutTabModel
import top.fifthlight.touchcontroller.ui.model.LayersTabModel
import top.fifthlight.touchcontroller.ui.state.CustomControlLayoutTabState

object LayersTab: CustomTab() {
    @Composable
    override fun Icon() {
        Icon(Textures.ICON_LAYER)
    }

    @Composable
    override fun Content() {
        val (screenModel, uiState, tabsButton, sideBarAtRight) = LocalCustomTabContext.current
        val tabModel: LayersTabModel = koinScreenModel { parametersOf(screenModel) }
        SideBarContainer(
            sideBarAtRight = sideBarAtRight,
            tabsButton = tabsButton,
            actions = {
                val currentLayer = uiState.selectedLayer
                IconButton(
                    onClick = {
                        tabModel.addLayer()
                    }
                ) {
                    Icon(Textures.ICON_ADD)
                }
                IconButton(
                    onClick = {
                        currentLayer?.let(tabModel::addLayer)
                    },
                    enabled = currentLayer != null,
                ) {
                    Icon(Textures.ICON_COPY)
                }
                IconButton(
                    onClick = {

                    },
                    enabled = currentLayer != null,
                ) {
                    Icon(Textures.ICON_CONFIG)
                }
                IconButton(
                    onClick = {
                        uiState.pageState.selectedLayerIndex.let(tabModel::removeLayer)
                    },
                    enabled = currentLayer != null,
                ) {
                    Icon(Textures.ICON_DELETE)
                }
            }
        ) { modifier ->
            SideBarScaffold(
                modifier = modifier,
                title = {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_LAYERS))
                },
                actions = {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {

                        }
                    ) {
                        Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_LAYERS_MOVE_UP))
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {

                        }
                    ) {
                        Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_LAYERS_MOVE_DOWN))
                    }
                }
            ) {

            }
        }
    }
}