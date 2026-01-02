package top.fifthlight.combine.theme.oreui

import top.fifthlight.combine.theme.Theme
import top.fifthlight.combine.ui.style.ColorTheme
import top.fifthlight.combine.ui.style.DrawableSet

val OreUITheme = run {
    val textures = OreUITexturesFactory.of()
    Theme(
        drawables = Theme.Drawables(
            button = DrawableSet(
                normal = textures.widget_button_button,
            ),
            guideButton = DrawableSet(
                normal = textures.widget_button_button_guide,
            ),
            itemGridBackground = textures.background_backpack,
        ),
        colors = Theme.Colors(
            button = ColorTheme.light,
        ),
    )
}
