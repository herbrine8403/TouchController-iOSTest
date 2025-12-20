package top.fifthlight.combine.paint

import androidx.compose.runtime.Immutable
import top.fifthlight.data.*

@Immutable
abstract class Texture(
    override val size: IntSize,
) : Drawable {
    override val padding: IntPadding
        get() = IntPadding.ZERO

    abstract fun Canvas.draw(
        dstRect: Rect,
        tint: Color = Colors.WHITE,
        srcRect: Rect,
    )

    fun Canvas.draw(
        dstRect: IntRect,
        tint: Color = Colors.WHITE,
        srcRect: IntRect,
    ) {
        draw(
            dstRect = dstRect.toRect(),
            tint = tint,
            srcRect = srcRect.toRect(),
        )
    }
}

@Immutable
data class NinePatchTexture(
    val texture: Texture,
    val scaleArea: IntRect,
    override val padding: IntPadding,
): Drawable {
    override val size: IntSize
        get() = texture.size

    override fun Canvas.draw(
        dstRect: IntRect,
        tint: Color,
    ) {
        val textureSize = texture.size
        val dstScaleAreaSize = dstRect.size - (texture.size - scaleArea.size)
        val srcBottomRightCornerOffset = scaleArea.offset + IntOffset(scaleArea.size.width, scaleArea.size.height)
        val dstBottomRightCornerOffset = dstRect.size - (textureSize - srcBottomRightCornerOffset)

        fun Texture.drawRegion(src: IntRect, dst: IntRect) {
            draw(
                dstRect = IntRect(
                    offset = dstRect.offset + dst.offset,
                    size = dst.size,
                ),
                tint = tint,
                srcRect = src,
            )
        }

        // Top-left corner
        texture.drawRegion(
            src = IntRect(
                offset = IntOffset.ZERO,
                size = IntSize(scaleArea.offset.left, scaleArea.offset.top)
            ),
            dst = IntRect(
                offset = dstRect.offset,
                size = IntSize(scaleArea.offset.left, scaleArea.offset.top)
            )
        )

        // Top edge
        texture.drawRegion(
            src = IntRect(
                offset = IntOffset(scaleArea.left, 0),
                size = IntSize(scaleArea.size.width, scaleArea.top)
            ),
            dst = IntRect(
                offset = dstRect.offset + IntOffset(scaleArea.left, 0),
                size = IntSize(dstScaleAreaSize.width, scaleArea.top)
            )
        )

        // Top-right corner
        texture.drawRegion(
            src = IntRect(
                offset = IntOffset(scaleArea.right, 0),
                size = IntSize(textureSize.width - scaleArea.right, scaleArea.top)
            ),
            dst = IntRect(
                offset = dstRect.offset + IntOffset(dstBottomRightCornerOffset.left, dstRect.top),
                size = IntSize(textureSize.width - scaleArea.right, scaleArea.top)
            )
        )

        // Middle-left edge
        texture.drawRegion(
            src = IntRect(
                offset = IntOffset(0, scaleArea.top),
                size = IntSize(scaleArea.left, scaleArea.size.height)
            ),
            dst = IntRect(
                offset = dstRect.offset + IntOffset(0, scaleArea.top),
                size = IntSize(scaleArea.left, dstScaleAreaSize.height)
            )
        )

        // Middle-center (scale area)
        texture.drawRegion(
            src = scaleArea,
            dst = IntRect(
                offset = dstRect.offset + scaleArea.offset,
                size = dstScaleAreaSize
            )
        )

        // Middle-right edge
        texture.drawRegion(
            src = IntRect(
                offset = IntOffset(scaleArea.right, scaleArea.top),
                size = IntSize(textureSize.width - scaleArea.right, scaleArea.size.height)
            ),
            dst = IntRect(
                offset = dstRect.offset + IntOffset(dstBottomRightCornerOffset.x, scaleArea.top),
                size = IntSize(textureSize.width - scaleArea.right, dstScaleAreaSize.height)
            )
        )

        // Bottom-left corner
        texture.drawRegion(
            src = IntRect(
                offset = IntOffset(0, scaleArea.bottom),
                size = IntSize(scaleArea.left, textureSize.height - scaleArea.bottom)
            ),
            dst = IntRect(
                offset = dstRect.offset + IntOffset(0, scaleArea.top + dstScaleAreaSize.height),
                size = IntSize(scaleArea.left, textureSize.height - scaleArea.bottom)
            )
        )

        // Bottom edge
        texture.drawRegion(
            src = IntRect(
                offset = IntOffset(scaleArea.left, scaleArea.bottom),
                size = IntSize(scaleArea.size.width, textureSize.height - scaleArea.bottom)
            ),
            dst = IntRect(
                offset = dstRect.offset + IntOffset(scaleArea.left, scaleArea.top + dstScaleAreaSize.height),
                size = IntSize(dstScaleAreaSize.width, textureSize.height - scaleArea.bottom)
            )
        )

        // Bottom-right corner
        texture.drawRegion(
            src = IntRect(
                offset = IntOffset(scaleArea.right, scaleArea.bottom),
                size = IntSize(textureSize.width - scaleArea.right, textureSize.height - scaleArea.bottom)
            ),
            dst = IntRect(
                offset = dstRect.offset + IntOffset(
                    dstBottomRightCornerOffset.x,
                    scaleArea.top + dstScaleAreaSize.height
                ),
                size = IntSize(textureSize.width - scaleArea.right, textureSize.height - scaleArea.bottom)
            )
        )
    }
}

@Immutable
abstract class BackgroundTexture(
    override val size: IntSize,
) : Drawable {
    override val padding: IntPadding
        get() = IntPadding.ZERO

    fun Canvas.draw(dstRect: IntRect, tint: Color = Colors.WHITE, scale: Float) = draw(dstRect.toRect(), tint, scale)
    abstract fun Canvas.draw(dstRect: Rect, tint: Color = Colors.WHITE, scale: Float)
}
