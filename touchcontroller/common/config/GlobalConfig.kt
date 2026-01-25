package top.fifthlight.touchcontroller.common.config

import kotlinx.serialization.Serializable
import top.fifthlight.touchcontroller.common.gal.itemlist.DefaultItemListProvider
import top.fifthlight.touchcontroller.common.gal.itemlist.DefaultItemListProviderFactory

@Serializable
data class GlobalConfig(
    val regular: RegularConfig = RegularConfig(),
    val control: ControlConfig = ControlConfig(),
    val touchRing: TouchRingConfig = TouchRingConfig(),
    val debug: DebugConfig = DebugConfig(),
    val item: ItemConfig = ItemConfig.default(DefaultItemListProviderFactory.of()),
    val preset: PresetConfig = PresetConfig.BuiltIn(),
    val chat: ChatConfig = ChatConfig(),
) {
    companion object {
        val default by lazy {
            GlobalConfig()
        }
    }
}
