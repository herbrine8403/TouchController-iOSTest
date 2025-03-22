package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.collections.immutable.PersistentList
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.input.MutableInteractionSource
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.modifier.Constraints
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.drawing.clip
import top.fifthlight.combine.modifier.focus.focusable
import top.fifthlight.combine.modifier.placement.fillMaxSize
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.node.LocalTextMeasurer
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.paint.Drawable
import top.fifthlight.combine.widget.base.Popup
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.data.IntRect
import top.fifthlight.data.IntSize
import kotlin.math.max

@JvmName("DropdownMenuListString")
@Composable
fun DropdownMenuScope.DropdownItemList(
    modifier: Modifier = Modifier,
    items: PersistentList<Pair<Text, () -> Unit>>,
) {
    DropdownItemList(
        modifier = modifier,
        items = items,
        textProvider = { it.first },
        onItemSelected = { items[it].second() },
    )
}

@Composable
fun <T> DropdownMenuScope.DropdownItemList(
    modifier: Modifier = Modifier,
    drawableSet: SelectDrawableSet = LocalSelectDrawableSet.current,
    items: List<T>,
    textProvider: (T) -> Text,
    selectedIndex: Int = -1,
    onItemSelected: (Int) -> Unit = {},
) {
    val itemTextureWidth = drawableSet.itemUnselected.normal.padding.width
    val itemTextureHeight = drawableSet.itemUnselected.normal.padding.height
    val textMeasurer = LocalTextMeasurer.current
    Layout(
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            var itemWidth = contentWidth
            var itemHeight = 0
            var itemHeights = IntArray(measurables.size)
            for ((index, item) in items.withIndex()) {
                val textSize = textMeasurer.measure(textProvider(item))
                val width = textSize.width + itemTextureWidth
                val height = textSize.height + itemTextureHeight
                itemHeights[index] = height
                itemWidth = max(width, itemWidth)
                itemHeight += height
            }

            val width = itemWidth.coerceIn(constraints.minWidth, constraints.maxWidth)
            val height = itemHeight.coerceIn(constraints.minHeight, constraints.maxHeight)

            val placeables = measurables.mapIndexed { index, measurable ->
                measurable.measure(
                    Constraints(
                        minWidth = width,
                        maxWidth = width,
                        minHeight = itemHeights[index],
                        maxHeight = itemHeights[index],
                    )
                )
            }
            layout(width, height) {
                var yPos = 0
                placeables.forEachIndexed { index, placeable ->
                    placeable.placeAt(0, yPos)
                    yPos += placeable.height
                }
            }
        },
    ) {
        for ((index, item) in items.withIndex()) {
            val text = textProvider(item)
            val interactionSource = remember { MutableInteractionSource() }
            val state by widgetState(interactionSource)
            val drawable = if (index == selectedIndex) {
                drawableSet.itemSelected
            } else {
                drawableSet.itemUnselected
            }.getByState(state)
            Text(
                modifier = Modifier
                    .border(drawable)
                    .clickable(interactionSource) {
                        onItemSelected(index)
                    }
                    .focusable(interactionSource),
                color = if (index == selectedIndex) {
                    Colors.BLACK
                } else {
                    Colors.WHITE
                },
                text = text,
            )
        }
    }
}

interface DropdownMenuScope {
    val anchor: IntRect
    val panelBorder: Drawable
    val contentWidth: Int
}

private data class DropdownMenuScopeImpl(
    override val anchor: IntRect,
    override val panelBorder: Drawable
) : DropdownMenuScope {
    override val contentWidth = anchor.size.width - panelBorder.padding.width
}

@Composable
fun DropDownMenu(
    anchor: IntRect,
    border: Drawable = LocalSelectDrawableSet.current.floatPanel,
    expandProgress: Float = 1f,
    onDismissRequest: () -> Unit,
    content: @Composable DropdownMenuScope.() -> Unit,
) {
    Popup(onDismissRequest = onDismissRequest) {
        Layout(
            modifier = Modifier.fillMaxSize(),
            measurePolicy = { measurables, constraints ->
                val screenSize = IntSize(constraints.maxWidth, constraints.maxHeight)
                val childConstraints = constraints.copy(
                    minWidth = anchor.size.width,
                    minHeight = 0,
                    maxWidth = screenSize.width,
                    maxHeight = screenSize.height,
                )
                val placeables = measurables.map { it.measure(childConstraints) }
                val width = placeables.maxOfOrNull { it.width } ?: 0
                val height = (placeables.maxOfOrNull { it.height } ?: 0)
                val realHeight = (height * expandProgress).toInt()
                val left = if (anchor.left + width < screenSize.width) {
                    anchor.left
                } else {
                    (anchor.right - width).coerceAtLeast(0)
                }
                val top = if (height + anchor.bottom < screenSize.height) {
                    anchor.bottom
                } else {
                    (anchor.top - realHeight).coerceAtLeast(0)
                }
                layout(width, realHeight) {
                    placeables.forEach { it.placeAt(left, top) }
                }
            },
        ) {
            val scope = DropdownMenuScopeImpl(anchor, border)
            Box(
                modifier = Modifier
                    .border(border)
                    .clip(width = 1f, height = expandProgress, anchorOffset = anchor.offset)
            ) {
                content(scope)
            }
        }
    }
}