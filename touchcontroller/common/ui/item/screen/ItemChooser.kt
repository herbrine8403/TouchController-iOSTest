package top.fifthlight.touchcontroller.common.ui.item.screen

import androidx.compose.runtime.Composable
import top.fifthlight.combine.item.data.Item
import top.fifthlight.touchcontroller.common.gal.PlayerHandleFactory
import top.fifthlight.touchcontroller.common.ui.component.TouchControllerNavigator

@Composable
fun ItemChooser(onItemChosen: (Item) -> Unit) {
    val playerHandle = PlayerHandleFactory.current()
    TouchControllerNavigator(
        if (playerHandle == null) {
            DefaultItemListScreen(onItemChosen)
        } else {
            ItemListChooseScreen(onItemChosen)
        }
    )
}
