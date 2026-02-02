package top.fifthlight.touchcontroller.common.ui.layer.tab.all

import kotlinx.collections.immutable.persistentListOf
import top.fifthlight.touchcontroller.common.ui.layer.tab.builtin.BuiltInTab
import top.fifthlight.touchcontroller.common.ui.layer.tab.custom.CustomTab
import top.fifthlight.touchcontroller.common.ui.layer.tab.holdingitem.HoldingItemTab

val allLayerConditionTabs = persistentListOf(
    BuiltInTab,
    HoldingItemTab,
    CustomTab,
)