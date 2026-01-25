package top.fifthlight.touchcontroller.version_26_1.gal

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import top.fifthlight.combine.backend.minecraft_26_1.toVanilla
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.item.data.Item
import top.fifthlight.touchcontroller.common.gal.item.ItemSubclass


class ItemSubclassImpl<Clazz>(
    override val name: Text,
    override val configId: String,
    val clazz: Class<Clazz>,
) : ItemSubclass {
    override val id: String = clazz.simpleName

    override fun contains(item: Item) = clazz.isInstance(item.toVanilla())

    override val items: PersistentList<Item> by lazy {
        ItemProviderImpl.allItems.filter { it in this }.toPersistentList()
    }
}
