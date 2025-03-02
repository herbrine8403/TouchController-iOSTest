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

data class RadioDrawableSet(
    val unchecked: DrawableSet,
    val checked: DrawableSet,
)

val defaultRadioDrawableSet = RadioDrawableSet(
    unchecked = DrawableSet(
        normal = Textures.WIDGET_RADIO_RADIO,
        focus = Textures.WIDGET_RADIO_RADIO_HOVER,
        hover = Textures.WIDGET_RADIO_RADIO_HOVER,
        active = Textures.WIDGET_RADIO_RADIO_ACTIVE,
        disabled = Textures.WIDGET_RADIO_RADIO,
    ),
    checked = DrawableSet(
        normal = Textures.WIDGET_RADIO_RADIO_CHECKED,
        focus = Textures.WIDGET_RADIO_RADIO_CHECKED_HOVER,
        hover = Textures.WIDGET_RADIO_RADIO_CHECKED_HOVER,
        active = Textures.WIDGET_RADIO_RADIO_CHECKED_ACTIVE,
        disabled = Textures.WIDGET_RADIO_RADIO_CHECKED,
    )
)

val LocalRadioDrawableSet = staticCompositionLocalOf<RadioDrawableSet> { defaultRadioDrawableSet }

@Composable
fun RadioIcon(
    modifier: Modifier = Modifier,
    interactionSource: InteractionSource,
    drawableSet: RadioDrawableSet = LocalRadioDrawableSet.current,
    value: Boolean,
) {
    val currentDrawableSet = if (value) {
        drawableSet.checked
    } else {
        drawableSet.unchecked
    }
    val state by widgetState(interactionSource)
    val drawable = currentDrawableSet.getByState(state)

    Icon(
        modifier = modifier,
        drawable = drawable,
    )
}

@Composable
fun Radio(
    modifier: Modifier = Modifier,
    drawableSet: RadioDrawableSet = LocalRadioDrawableSet.current,
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

    RadioIcon(
        modifier = modifier,
        interactionSource = interactionSource,
        drawableSet = drawableSet,
        value = value,
    )
}