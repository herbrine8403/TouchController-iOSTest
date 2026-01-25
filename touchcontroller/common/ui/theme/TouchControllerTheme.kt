package top.fifthlight.touchcontroller.common.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import top.fifthlight.combine.paint.BackgroundTexture
import top.fifthlight.combine.paint.Drawable
import top.fifthlight.combine.theme.LocalTheme
import top.fifthlight.combine.theme.Theme
import top.fifthlight.combine.theme.blackstone.BlackstoneTextures
import top.fifthlight.combine.theme.blackstone.BlackstoneTheme
import top.fifthlight.touchcontroller.assets.Textures

val LocalTouchControllerTheme = staticCompositionLocalOf { TouchControllerTheme() }

data class TouchControllerTheme(
    val background: BackgroundTexture = Textures.background_brick_background,
    val backgroundDark: Drawable = BlackstoneTextures.widget_background_background_dark,
    val base: Theme = BlackstoneTheme,
) {
    companion object {
        val default = TouchControllerTheme()

        @Composable
        inline operator fun invoke(crossinline block: @Composable TouchControllerTheme.() -> Unit) {
            default(block)
        }
    }
}

@Composable
inline operator fun TouchControllerTheme.invoke(crossinline block: @Composable TouchControllerTheme.() -> Unit) {
    CompositionLocalProvider(
        LocalTouchControllerTheme provides TouchControllerTheme(),
        LocalTheme provides base,
    ) {
        block()
    }
}
