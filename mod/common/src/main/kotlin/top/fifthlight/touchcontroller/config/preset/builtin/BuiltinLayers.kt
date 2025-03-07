package top.fifthlight.touchcontroller.config.preset.builtin

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import top.fifthlight.data.IntOffset
import top.fifthlight.touchcontroller.config.*
import top.fifthlight.touchcontroller.control.*
import top.fifthlight.touchcontroller.layout.Align

object BuiltinLayers {
    data class BuiltinLayers(
        val name: String,
        val condition: LayoutLayerCondition,
        val dpadNormal: PersistentList<ControllerWidget>,
        val dpadSwap: PersistentList<ControllerWidget> = dpadNormal,
        val dpadNormalButtonInteract: PersistentList<ControllerWidget> = dpadNormal,
        val dpadSwapButtonInteract: PersistentList<ControllerWidget> = dpadSwap,
        val joystick: PersistentList<ControllerWidget> = dpadNormal,
    ) {
        fun getByKey(key: BuiltinPresetKey) = LayoutLayer(
            name = name,
            condition = condition,
            widgets = when (val moveMethod = key.moveMethod) {
                is BuiltinPresetKey.MoveMethod.Dpad -> if (key.controlStyle is BuiltinPresetKey.ControlStyle.SplitControls && key.controlStyle.buttonInteraction) {
                    if (moveMethod.swapJumpAndSneak) {
                        dpadSwapButtonInteract
                    } else {
                        dpadNormalButtonInteract
                    }
                } else {
                    if (moveMethod.swapJumpAndSneak) {
                        dpadSwap
                    } else {
                        dpadNormal
                    }
                }

                is BuiltinPresetKey.MoveMethod.Joystick -> joystick.map {
                    if (it is Joystick) {
                        it.copy(triggerSprint = moveMethod.triggerSprint)
                    } else {
                        it
                    }
                }.toPersistentList()
            }
        )
    }

    val controlLayer = LayoutLayer(
        name = "Control",
        condition = layoutLayerConditionOf(),
        widgets = persistentListOf(
            PauseButton(
                align = Align.CENTER_TOP,
                offset = IntOffset(-9, 0),
            ),
            ChatButton(
                align = Align.CENTER_TOP,
                offset = IntOffset(9, 0),
            ),
            InventoryButton(),
        )
    )

    val interactionLayer = LayoutLayer(
        name = "Interaction",
        condition = layoutLayerConditionOf(),
        widgets = persistentListOf(
            AttackButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(86, 70),
            ),
            UseButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 37),
            ),
        )
    )

    val sprintRightButton = SprintButton(
        align = Align.RIGHT_BOTTOM,
        offset = IntOffset(42, 131),
    )

    val sprintRightTopButton = SprintButton(
        align = Align.RIGHT_TOP,
        offset = IntOffset(42, 44),
    )

    val normalLayer = BuiltinLayers(
        name = "Normal",
        condition = layoutLayerConditionOf(
            LayerConditionKey.SWIMMING to LayerConditionValue.NEVER,
            LayerConditionKey.FLYING to LayerConditionValue.NEVER,
            LayerConditionKey.RIDING to LayerConditionValue.NEVER,
        ),
        dpadNormal = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(12, 16),
                extraButton = DPadExtraButton.SNEAK_DOUBLE_CLICK,
            ),
            JumpButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 68),
            ),
        ),
        dpadSwap = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(12, 16),
                extraButton = DPadExtraButton.JUMP,
            ),
            SneakButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 68),
            ),
        ),
        dpadNormalButtonInteract = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(12, 16),
                extraButton = DPadExtraButton.SNEAK_DOUBLE_CLICK,
            ),
            JumpButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 165),
            ),
            SneakButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 102),
            ),
        ),
        dpadSwapButtonInteract = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(12, 16),
                extraButton = DPadExtraButton.JUMP,
            ),
            JumpButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 165),
            ),
            SneakButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 102),
            ),
        ),
        joystick = persistentListOf(
            Joystick(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(29, 32),
            ),
            JumpButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 165),
            ),
            SneakButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 102),
            ),
        ),
    )

    val swimmingLayer = BuiltinLayers(
        name = "Swimming",
        condition = layoutLayerConditionOf(
            LayerConditionKey.RIDING to LayerConditionValue.NEVER,
            LayerConditionKey.FLYING to LayerConditionValue.NEVER,
            LayerConditionKey.SWIMMING to LayerConditionValue.WANT,
            LayerConditionKey.UNDERWATER to LayerConditionValue.WANT,
        ),
        dpadNormal = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(12, 16),
                extraButton = DPadExtraButton.NONE,
            ),
            AscendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 68),
                texture = AscendButtonTexture.SWIMMING,
            ),
            DescendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 18),
                texture = DescendButtonTexture.SWIMMING,
            )
        ),
        dpadSwap = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(12, 16),
                // TODO ascend button
                extraButton = DPadExtraButton.JUMP,
            ),
            DescendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 18),
                texture = DescendButtonTexture.SWIMMING,
            )
        ),
        dpadNormalButtonInteract = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(12, 16),
                extraButton = DPadExtraButton.NONE,
            ),
            AscendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 165),
                texture = AscendButtonTexture.SWIMMING,
            ),
            DescendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 18),
                texture = DescendButtonTexture.SWIMMING,
            )
        ),
        joystick = persistentListOf(
            Joystick(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(29, 32),
            ),
            JumpButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 165),
            ),
            SneakButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 102),
            ),
        ),
    )

    val flyingLayer = BuiltinLayers(
        name = "Flying",
        condition = layoutLayerConditionOf(
            LayerConditionKey.FLYING to LayerConditionValue.REQUIRE,
        ),
        dpadNormal = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(12, 16),
                extraButton = DPadExtraButton.NONE,
            ),
            AscendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 68),
                texture = AscendButtonTexture.FLYING,
            ),
            DescendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 18),
                texture = DescendButtonTexture.FLYING,
            )
        ),
        dpadSwap = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(12, 16),
                // TODO ascend button
                extraButton = DPadExtraButton.JUMP,
            ),
            DescendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 18),
                texture = DescendButtonTexture.FLYING,
            )
        ),
        dpadNormalButtonInteract = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(12, 16),
                extraButton = DPadExtraButton.NONE,
            ),
            AscendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 164),
                texture = AscendButtonTexture.SWIMMING,
            ),
            DescendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 102),
                texture = DescendButtonTexture.SWIMMING,
            )
        ),
        joystick = persistentListOf(
            Joystick(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(29, 32),
            ),
            AscendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 165),
            ),
            DescendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 102),
            ),
        ),
    )

    val onMinecartLayer = BuiltinLayers(
        name = "On minecart",
        condition = layoutLayerConditionOf(
            LayerConditionKey.ON_MINECART to LayerConditionValue.REQUIRE,
        ),
        dpadNormal = persistentListOf(
            ForwardButton(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(59, 111)
            ),
            SneakButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 68),
                texture = SneakButtonTexture.DISMOUNT,
            ),
        ),
        dpadSwap = persistentListOf(
            ForwardButton(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(59, 111)
            ),
            SneakButton(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(59, 63),
                texture = SneakButtonTexture.DISMOUNT,
            ),
        ),
        joystick = persistentListOf(
            Joystick(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(29, 32),
            ),
            SneakButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 165),
                trigger = SneakButtonTrigger.SINGLE_CLICK_TRIGGER,
                texture = SneakButtonTexture.DISMOUNT,
            ),
        ),
    )

    val onBoatLayer = BuiltinLayers(
        name = "On boat",
        condition = layoutLayerConditionOf(
            LayerConditionKey.ON_BOAT to LayerConditionValue.REQUIRE,
        ),
        dpadNormal = persistentListOf(
            BoatButton(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(16, 16),
                side = BoatButtonSide.LEFT,
            ),
            BoatButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(16, 16),
                side = BoatButtonSide.RIGHT,
            )
        ),
        joystick = persistentListOf(
            Joystick(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(29, 32),
            ),
            SneakButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 165),
                trigger = SneakButtonTrigger.SINGLE_CLICK_TRIGGER,
                texture = SneakButtonTexture.DISMOUNT,
            ),
        ),
    )

    val ridingOnEntityLayer = BuiltinLayers(
        name = "Riding on entity",
        condition = layoutLayerConditionOf(
            LayerConditionKey.RIDING to LayerConditionValue.REQUIRE,
            LayerConditionKey.ON_BOAT to LayerConditionValue.NEVER,
            LayerConditionKey.ON_MINECART to LayerConditionValue.NEVER,
        ),
        dpadNormal = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(12, 16),
                extraButton = DPadExtraButton.DISMOUNT_DOUBLE_CLICK,
            ),
            Joystick(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(29, 32),
            ),
            JumpButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 165),
            ),
            SneakButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 102),
                trigger = SneakButtonTrigger.SINGLE_CLICK_TRIGGER,
                texture = SneakButtonTexture.DISMOUNT,
            ),
        ),
        joystick = persistentListOf(
            Joystick(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(29, 32),
            ),
            JumpButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 165),
                texture = JumpButtonTexture.NEW_HORSE,
            ),
            SneakButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(22, 102),
                trigger = SneakButtonTrigger.SINGLE_CLICK_TRIGGER,
                texture = SneakButtonTexture.DISMOUNT,
            ),
        ),
    )
}
