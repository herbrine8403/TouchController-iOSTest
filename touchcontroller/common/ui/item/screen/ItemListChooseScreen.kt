package top.fifthlight.touchcontroller.common.ui.item.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.item.data.Item
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.fillMaxSize
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.widget.layout.Box
import top.fifthlight.combine.widget.layout.Column
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.combine.widget.ui.Text
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.common.gal.PlayerHandleFactory
import top.fifthlight.touchcontroller.common.gal.creativetab.CreativeTabsProvider
import top.fifthlight.touchcontroller.common.gal.creativetab.CreativeTabsProviderFactory

class ItemListChooseScreen(
    private val onItemSelected: (Item) -> Unit,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        Box(
            modifier = Modifier.fillMaxSize(),
            alignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(.6f),
                verticalArrangement = Arrangement.spacedBy(8),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(Text.translatable(Texts.SCREEN_ITEM_LIST_CONTENT))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navigator?.push(DefaultItemListScreen(onItemSelected))
                    }
                ) {
                    Text(Text.translatable(Texts.SCREEN_ITEM_LIST_DEFAULT))
                }

                val player = PlayerHandleFactory.current()
                if (player != null) {
                    val vanillaItemListProvider: CreativeTabsProvider = CreativeTabsProviderFactory.of()
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val tabs = vanillaItemListProvider.getCreativeTabs(player)
                            val playerInventory = player.getInventory()
                            navigator?.push(VanillaItemListScreen(onItemSelected, tabs, playerInventory))
                        }
                    ) {
                        Text(Text.translatable(Texts.SCREEN_ITEM_LIST_VANILLA_INVENTORY))
                    }
                }
            }
        }
    }
}