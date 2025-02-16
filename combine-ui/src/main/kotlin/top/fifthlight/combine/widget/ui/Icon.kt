package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.Composable
import top.fifthlight.combine.data.Texture
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.size
import top.fifthlight.combine.widget.base.Canvas
import top.fifthlight.data.IntSize
import top.fifthlight.data.Offset
import top.fifthlight.data.Rect

@Composable
fun Icon(
    texture: Texture,
    modifier: Modifier = Modifier,
    size: IntSize = texture.size,
) {
    Canvas(modifier.size(size)) {
        canvas.drawTexture(
            texture = texture,
            dstRect = Rect(
                offset = Offset.ZERO,
                size = size.toSize(),
            )
        )
    }
}
