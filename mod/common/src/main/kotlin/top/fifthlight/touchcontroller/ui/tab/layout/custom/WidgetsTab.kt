package top.fifthlight.touchcontroller.ui.tab.layout.custom

import androidx.compose.runtime.Composable
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.combine.widget.ui.Icon
import top.fifthlight.combine.widget.ui.IconButton
import top.fifthlight.combine.widget.ui.Text
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.assets.Textures

object WidgetsTab : CustomTab() {
    @Composable
    override fun Icon() {
        Icon(Textures.ICON_WIDGET)
    }

    @Composable
    override fun Content() {
        val (screenModel, uiState, tabsButton, sideBarAtRight) = LocalCustomTabContext.current
        SideBarContainer(
            sideBarAtRight = sideBarAtRight,
            tabsButton = tabsButton,
            actions = {
                IconButton(
                    onClick = {}
                ) {
                    Icon(Textures.ICON_CONFIG)
                }
            }
        ) { modifier ->
            SideBarScaffold(
                modifier = modifier,
                title = {
                    Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_WIDGETS))
                },
                actions = {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {

                        }
                    ) {
                        Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_WIDGETS_BUILTIN))
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {

                        }
                    ) {
                        Text(Text.translatable(Texts.SCREEN_CUSTOM_CONTROL_LAYOUT_WIDGETS_PRESET))
                    }
                }
            ) {

            }
        }
    }
}