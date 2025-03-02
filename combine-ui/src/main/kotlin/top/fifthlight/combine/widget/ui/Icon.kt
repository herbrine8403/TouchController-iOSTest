package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.Composable
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.size
import top.fifthlight.combine.paint.Drawable
import top.fifthlight.combine.widget.base.Canvas
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntRect
import top.fifthlight.data.IntSize

@Composable
fun Icon(
    drawable: Drawable,
    modifier: Modifier = Modifier,
    size: IntSize = drawable.size,
) {
    Canvas(modifier.size(size)) {
        drawable.run { draw(IntRect(offset = IntOffset.ZERO, size = size)) }
    }
}
