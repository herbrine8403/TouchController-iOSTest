package top.fifthlight.touchcontroller.ui.state

import top.fifthlight.touchcontroller.config.ControllerLayout

sealed class LayersTabState {
    data object Empty : LayersTabState()

    data class Create(
        val layer: ControllerLayout = ControllerLayout(),
    ):LayersTabState()

    data class Edit(
        val layer: ControllerLayout = ControllerLayout(),
    ) : LayersTabState()
}
