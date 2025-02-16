package top.fifthlight.touchcontroller.ui.component.config.layout

import androidx.compose.runtime.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.widget.ui.Select
import top.fifthlight.combine.widget.ui.SelectIcon
import top.fifthlight.combine.widget.ui.SelectItemList
import top.fifthlight.combine.widget.ui.Text
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.config.LayoutLayer

@Composable
fun LayerDropdown(
    modifier: Modifier = Modifier,
    currentLayerIndex: Int = -1,
    currentLayer: LayoutLayer? = null,
    allLayers: PersistentList<LayoutLayer> = persistentListOf(),
    onLayerSelected: (Int, LayoutLayer) -> Unit = { _, _ -> },
) {
    var expanded by remember { mutableStateOf(false) }
    Select(
        modifier = modifier,
        expanded = expanded,
        onExpandedChanged = { expanded = it },
        dropDownContent = {
            SelectItemList(
                modifier = Modifier.verticalScroll(),
                items = allLayers,
                stringProvider = LayoutLayer::name,
                selectedIndex = currentLayerIndex,
                onItemSelected = {
                    onLayerSelected(it, allLayers[it])
                    expanded = false
                },
            )
        }
    ) {
        if (currentLayer == null) {
            Text(text = Text.translatable(Texts.SCREEN_OPTIONS_WIDGET_NO_LAYER_SELECTED_TITLE))
        } else {
            Text(text = currentLayer.name)
        }
        SelectIcon(expanded = expanded)
    }
}