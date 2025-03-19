package top.fifthlight.combine.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenAccessor

@Composable
fun <T: TweenAccessor> animateValueAsState(targetValue: T): State<T> {
    val state = remember { mutableStateOf(targetValue) }
    DisposableEffect(targetValue) {
        val tween = if (state.value != targetValue) {
            Tween.to(state.value, )
        } else {
            null
        }
        onDispose {

        }
    }
    return state
}