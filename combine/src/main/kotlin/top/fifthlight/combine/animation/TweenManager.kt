package top.fifthlight.combine.animation

import androidx.compose.runtime.staticCompositionLocalOf
import aurelienribon.tweenengine.TweenManager

val LocalTweenManager = staticCompositionLocalOf<TweenManager> { error("No tween manager in scope") }
