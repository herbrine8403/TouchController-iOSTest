package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import top.fifthlight.combine.input.InteractionSource
import top.fifthlight.combine.input.MutableInteractionSource
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.focus.focusable
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.ui.style.DrawableSet
import top.fifthlight.touchcontroller.assets.Textures

data class CheckBoxDrawableSet(
    val unchecked: DrawableSet,
    val checked: DrawableSet,
)

val defaultCheckBoxDrawableSet = CheckBoxDrawableSet(
    unchecked = DrawableSet(
        normal = Textures.WIDGET_CHECKBOX_CHECKBOX,
        focus = Textures.WIDGET_CHECKBOX_CHECKBOX_HOVER,
        hover = Textures.WIDGET_CHECKBOX_CHECKBOX_HOVER,
        active = Textures.WIDGET_CHECKBOX_CHECKBOX_ACTIVE,
        disabled = Textures.WIDGET_CHECKBOX_CHECKBOX,
    ),
    checked = DrawableSet(
        normal = Textures.WIDGET_CHECKBOX_CHECKBOX_CHECKED,
        focus = Textures.WIDGET_CHECKBOX_CHECKBOX_CHECKED_HOVER,
        hover = Textures.WIDGET_CHECKBOX_CHECKBOX_CHECKED_HOVER,
        active = Textures.WIDGET_CHECKBOX_CHECKBOX_CHECKED_ACTIVE,
        disabled = Textures.WIDGET_CHECKBOX_CHECKBOX_CHECKED,
    )
)

val LocalCheckBoxDrawableSet = staticCompositionLocalOf<CheckBoxDrawableSet> { defaultCheckBoxDrawableSet }

@Composable
fun CheckBoxIcon(
    modifier: Modifier = Modifier,
    interactionSource: InteractionSource,
    DrawableSet: CheckBoxDrawableSet = LocalCheckBoxDrawableSet.current,
    value: Boolean,
) {
    val currentDrawableSet = if (value) {
        DrawableSet.checked
    } else {
        DrawableSet.unchecked
    }
    val state by widgetState(interactionSource)
    val drawable = currentDrawableSet.getByState(state)

    Icon(
        modifier = modifier,
        drawable = drawable,
    )
}

@Composable
fun CheckBox(
    modifier: Modifier = Modifier,
    DrawableSet: CheckBoxDrawableSet = LocalCheckBoxDrawableSet.current,
    value: Boolean,
    onValueChanged: ((Boolean) -> Unit)?,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val modifier = if (onValueChanged == null) {
        modifier
    } else {
        Modifier
            .clickable(interactionSource) {
                onValueChanged(!value)
            }
            .focusable(interactionSource)
            .then(modifier)
    }

    CheckBoxIcon(
        modifier = modifier,
        interactionSource = interactionSource,
        DrawableSet = DrawableSet,
        value = value,
    )
}