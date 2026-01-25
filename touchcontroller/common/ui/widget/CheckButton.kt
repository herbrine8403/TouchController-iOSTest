package top.fifthlight.touchcontroller.common.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.theme.blackstone.BlackstoneTextures
import top.fifthlight.combine.ui.style.ColorTheme
import top.fifthlight.combine.ui.style.DrawableSet
import top.fifthlight.combine.widget.layout.BoxScope
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.data.IntPadding
import top.fifthlight.data.IntSize

// TODO: should use some theme system

data class CheckButtonDrawables(
    val unchecked: DrawableSet,
    val checked: DrawableSet,
)

data class CheckButtonColors(
    val unchecked: ColorTheme,
    val checked: ColorTheme,
)

val tabButtonDrawable = CheckButtonDrawables(
    unchecked = DrawableSet(
        normal = BlackstoneTextures.widget_tab_tab,
        focus = BlackstoneTextures.widget_tab_tab_hover,
        hover = BlackstoneTextures.widget_tab_tab_hover,
        active = BlackstoneTextures.widget_tab_tab_active,
        disabled = BlackstoneTextures.widget_tab_tab_disabled,
    ),
    checked = DrawableSet(
        normal = BlackstoneTextures.widget_tab_tab_presslock,
        focus = BlackstoneTextures.widget_tab_tab_presslock_hover,
        hover = BlackstoneTextures.widget_tab_tab_presslock_hover,
        active = BlackstoneTextures.widget_tab_tab_active,
        disabled = BlackstoneTextures.widget_tab_tab_disabled,
    ),
)

val tabButtonColors = CheckButtonColors(
    unchecked = ColorTheme.dark,
    checked = ColorTheme.light,
)

val listButtonDrawable = CheckButtonDrawables(
    unchecked = DrawableSet(
        normal = BlackstoneTextures.widget_list_list,
        focus = BlackstoneTextures.widget_list_list_hover,
        hover = BlackstoneTextures.widget_list_list_hover,
        active = BlackstoneTextures.widget_list_list_active,
        disabled = BlackstoneTextures.widget_list_list_disabled,
    ),
    checked = DrawableSet(
        normal = BlackstoneTextures.widget_list_list_presslock,
        focus = BlackstoneTextures.widget_list_list_presslock_hover,
        hover = BlackstoneTextures.widget_list_list_presslock_hover,
        active = BlackstoneTextures.widget_list_list_active,
        disabled = BlackstoneTextures.widget_list_list_disabled,
    ),
)

val listButtonColors = CheckButtonColors(
    unchecked = ColorTheme.dark,
    checked = ColorTheme.light,
)

val checkButtonDrawable = CheckButtonDrawables(
    unchecked = DrawableSet(
        normal = BlackstoneTextures.widget_button_button,
        hover = BlackstoneTextures.widget_button_button_hover,
        focus = BlackstoneTextures.widget_button_button_hover,
        active = BlackstoneTextures.widget_button_button_active,
    ),
    checked = DrawableSet(
        normal = BlackstoneTextures.widget_button_button_presslock,
        hover = BlackstoneTextures.widget_button_button_presslock_hover,
        focus = BlackstoneTextures.widget_button_button_presslock_hover,
        active = BlackstoneTextures.widget_button_button_presslock_active,
    ),
)

val checkButtonColors = CheckButtonColors(
    unchecked = ColorTheme.light,
    checked = ColorTheme.light,
)

val LocalTabButtonDrawable = staticCompositionLocalOf { tabButtonDrawable }
val LocalTabButtonColors = staticCompositionLocalOf { tabButtonColors }
val LocalListButtonDrawable = staticCompositionLocalOf { listButtonDrawable }
val LocalListButtonColors = staticCompositionLocalOf { listButtonColors }
val LocalCheckButtonDrawable = staticCompositionLocalOf { checkButtonDrawable }
val LocalCheckButtonColors = staticCompositionLocalOf { checkButtonColors }

@Composable
fun TabButton(
    modifier: Modifier = Modifier,
    drawableSet: CheckButtonDrawables = LocalTabButtonDrawable.current,
    colors: CheckButtonColors = LocalTabButtonColors.current,
    checked: Boolean = false,
    minSize: IntSize = IntSize(48, 20),
    padding: IntPadding = IntPadding(left = 4, right = 4, top = 1),
    enabled: Boolean = true,
    onClick: () -> Unit,
    clickSound: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) = CheckButton(
    modifier = modifier,
    drawableSet = drawableSet,
    colors = colors,
    checked = checked,
    padding = padding,
    enabled = enabled,
    minSize = minSize,
    onClick = onClick,
    clickSound = clickSound,
    content = content,
)

@Composable
fun ListButton(
    modifier: Modifier = Modifier,
    drawableSet: CheckButtonDrawables = LocalListButtonDrawable.current,
    colors: CheckButtonColors = LocalListButtonColors.current,
    checked: Boolean = false,
    minSize: IntSize = IntSize(48, 20),
    padding: IntPadding = IntPadding(left = 4, right = 4, top = 1),
    enabled: Boolean = true,
    onClick: () -> Unit,
    clickSound: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) = CheckButton(
    modifier = modifier,
    drawableSet = drawableSet,
    colors = colors,
    checked = checked,
    minSize = minSize,
    padding = padding,
    enabled = enabled,
    onClick = onClick,
    clickSound = clickSound,
    content = content,
)

@Composable
fun CheckButton(
    modifier: Modifier = Modifier,
    drawableSet: CheckButtonDrawables = LocalCheckButtonDrawable.current,
    colors: CheckButtonColors = LocalCheckButtonColors.current,
    checked: Boolean = false,
    minSize: IntSize = IntSize(48, 20),
    padding: IntPadding = IntPadding(left = 4, right = 4, top = 1),
    enabled: Boolean = true,
    onClick: () -> Unit,
    clickSound: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) = Button(
    modifier = modifier,
    drawableSet = if (checked) {
        drawableSet.checked
    } else {
        drawableSet.unchecked
    },
    colorTheme = if (checked) {
        colors.checked
    } else {
        colors.unchecked
    },
    minSize = minSize,
    padding = padding,
    enabled = enabled,
    onClick = onClick,
    clickSound = clickSound,
    content = content,
)