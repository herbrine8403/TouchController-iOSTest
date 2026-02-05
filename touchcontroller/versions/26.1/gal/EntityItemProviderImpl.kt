package top.fifthlight.touchcontroller.version_26_1.gal

import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Items
import top.fifthlight.combine.backend.minecraft_26_1.toCombine
import net.minecraft.world.item.SpawnEggItem
import top.fifthlight.mergetools.api.ActualConstructor
import top.fifthlight.mergetools.api.ActualImpl
import top.fifthlight.touchcontroller.common.gal.entity.EntityItemProvider
import top.fifthlight.touchcontroller.common.gal.entity.EntityType as TouchControllerEntityType
import kotlin.jvm.optionals.getOrNull

@ActualImpl(EntityItemProvider::class)
object EntityItemProviderImpl : EntityItemProvider {
    @JvmStatic
    @ActualConstructor
    fun of(): EntityItemProvider = this

    override fun getEntityIconItem(entity: TouchControllerEntityType) =
        when (val entityType = (entity as EntityTypeImpl).inner) {
            EntityType.PAINTING -> Items.PAINTING
            EntityType.ARMOR_STAND -> Items.ARMOR_STAND
            EntityType.MINECART -> Items.MINECART
            EntityType.TNT_MINECART -> Items.TNT_MINECART
            EntityType.CHEST_MINECART -> Items.CHEST_MINECART
            EntityType.HOPPER_MINECART -> Items.HOPPER_MINECART
            EntityType.FURNACE_MINECART -> Items.FURNACE_MINECART
            EntityType.COMMAND_BLOCK_MINECART -> Items.COMMAND_BLOCK_MINECART
            EntityType.AREA_EFFECT_CLOUD -> Items.LINGERING_POTION
            EntityType.ARROW -> Items.ARROW
            EntityType.ITEM_FRAME -> Items.ITEM_FRAME
            EntityType.PLAYER -> Items.PLAYER_HEAD
            EntityType.WIND_CHARGE -> Items.WIND_CHARGE
            EntityType.SNOWBALL -> Items.SNOWBALL
            EntityType.EGG -> Items.EGG
            EntityType.FIREBALL -> Items.FIRE_CHARGE
            EntityType.FIREWORK_ROCKET -> Items.FIREWORK_ROCKET
            EntityType.TNT -> Items.TNT
            EntityType.SMALL_FIREBALL -> Items.FIREWORK_ROCKET
            // TODO
            else -> SpawnEggItem.byId(entityType).getOrNull()?.value()
        }?.toCombine()
}
