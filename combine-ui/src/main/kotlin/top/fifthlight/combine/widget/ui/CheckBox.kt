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
import top.fifthlight.combine.ui.style.TextureSet
import top.fifthlight.touchcontroller.assets.Textures

data class CheckBoxTextureSet(
    val unchecked: TextureSet,
    val checked: TextureSet,
)

val defaultCheckBoxTextureSet = CheckBoxTextureSet(
    unchecked = TextureSet(
        normal = Textures.WIDGET_CHECKBOX_CHECKBOX,
        focus = Textures.WIDGET_CHECKBOX_CHECKBOX_HOVER,
        hover = Textures.WIDGET_CHECKBOX_CHECKBOX_HOVER,
        active = Textures.WIDGET_CHECKBOX_CHECKBOX_ACTIVE,
        disabled = Textures.WIDGET_CHECKBOX_CHECKBOX,
    ),
    checked = TextureSet(
        normal = Textures.WIDGET_CHECKBOX_CHECKBOX_CHECKED,
        focus = Textures.WIDGET_CHECKBOX_CHECKBOX_CHECKED_HOVER,
        hover = Textures.WIDGET_CHECKBOX_CHECKBOX_CHECKED_HOVER,
        active = Textures.WIDGET_CHECKBOX_CHECKBOX_CHECKED_ACTIVE,
        disabled = Textures.WIDGET_CHECKBOX_CHECKBOX_CHECKED,
    )
)

val LocalCheckBoxTextureSet = staticCompositionLocalOf<CheckBoxTextureSet> { defaultCheckBoxTextureSet }

@Composable
fun CheckBoxIcon(
    modifier: Modifier = Modifier,
    interactionSource: InteractionSource,
    textureSet: CheckBoxTextureSet = LocalCheckBoxTextureSet.current,
    value: Boolean,
) {
    val currentTextureSet = if (value) {
        textureSet.checked
    } else {
        textureSet.unchecked
    }
    val state by widgetState(interactionSource)
    val texture = currentTextureSet.getByState(state)

    Icon(
        modifier = modifier,
        texture = texture
    )
}

@Composable
fun CheckBox(
    modifier: Modifier = Modifier,
    textureSet: CheckBoxTextureSet = LocalCheckBoxTextureSet.current,
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
        textureSet = textureSet,
        value = value,
    )
}