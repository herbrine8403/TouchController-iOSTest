package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.*
import top.fifthlight.combine.data.LocalTextFactory
import top.fifthlight.combine.data.NinePatchTexture
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.input.MutableInteractionSource
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.modifier.Constraints
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.focus.focusable
import top.fifthlight.combine.modifier.placement.*
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.modifier.pointer.consumePress
import top.fifthlight.combine.node.LocalTextMeasurer
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.ui.style.ColorTheme
import top.fifthlight.combine.ui.style.DrawableSet
import top.fifthlight.combine.ui.style.LocalColorTheme
import top.fifthlight.combine.widget.base.Popup
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.RowScope
import top.fifthlight.data.IntRect
import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.assets.Textures
import kotlin.math.max

data class SelectDrawableSet(
    val menuBox: DrawableSet,
    val floatPanel: NinePatchTexture,
    val itemUnselected: DrawableSet,
    val itemSelected: DrawableSet,
)

val defaultSelectDrawableSet = SelectDrawableSet(
    menuBox = DrawableSet(
        normal = Textures.WIDGET_SELECT_SELECT,
        focus = Textures.WIDGET_SELECT_SELECT_HOVER,
        hover = Textures.WIDGET_SELECT_SELECT_HOVER,
        active = Textures.WIDGET_SELECT_SELECT_ACTIVE,
        disabled = Textures.WIDGET_SELECT_SELECT_DISABLED,
    ),
    floatPanel = Textures.WIDGET_BACKGROUND_FLOAT_WINDOW,
    itemUnselected = DrawableSet(
        normal = Textures.WIDGET_LIST_LIST,
        focus = Textures.WIDGET_LIST_LIST_HOVER,
        hover = Textures.WIDGET_LIST_LIST_HOVER,
        active = Textures.WIDGET_LIST_LIST_ACTIVE,
        disabled = Textures.WIDGET_LIST_LIST_DISABLED,
    ),
    itemSelected = DrawableSet(
        normal = Textures.WIDGET_LIST_LIST_PRESSLOCK,
        focus = Textures.WIDGET_LIST_LIST_PRESSLOCK_HOVER,
        hover = Textures.WIDGET_LIST_LIST_PRESSLOCK_HOVER,
        active = Textures.WIDGET_LIST_LIST_ACTIVE,
        disabled = Textures.WIDGET_LIST_LIST_DISABLED,
    )
)

val LocalSelectDrawableSet = staticCompositionLocalOf<SelectDrawableSet> { defaultSelectDrawableSet }

@Composable
fun SelectIcon(
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier,
        drawable = if (expanded) {
            Textures.ICON_UP
        } else {
            Textures.ICON_DOWN
        }
    )
}

@JvmName("DropdownMenuListString")
@Composable
fun <T> SelectScope.SelectItemList(
    modifier: Modifier = Modifier,
    items: List<T>,
    stringProvider: (T) -> String,
    selectedIndex: Int = -1,
    onItemSelected: (Int) -> Unit = {},
) {
    val textFactory = LocalTextFactory.current
    SelectItemList(
        modifier = modifier,
        items = items,
        textProvider = { textFactory.literal(stringProvider(it)) },
        selectedIndex = selectedIndex,
        onItemSelected = onItemSelected,
    )
}

@Composable
fun <T> SelectScope.SelectItemList(
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

interface SelectScope {
    val anchor: IntRect
    val drawableSet: SelectDrawableSet
    val contentWidth: Int
}

private data class SelectScopeImpl(
    override val anchor: IntRect,
    override val drawableSet: SelectDrawableSet
): SelectScope {
    override val contentWidth = anchor.size.width - drawableSet.floatPanel.padding.width
}

@Composable
fun Select(
    modifier: Modifier = Modifier,
    drawableSet: SelectDrawableSet = LocalSelectDrawableSet.current,
    colorTheme: ColorTheme? = null,
    expanded: Boolean = false,
    onExpandedChanged: (Boolean) -> Unit,
    dropDownContent: @Composable SelectScope.() -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val state by widgetState(interactionSource)
    val menuTexture = drawableSet.menuBox.getByState(state)

    var anchor by remember { mutableStateOf<IntRect?>(null) }
    val colorTheme = colorTheme ?: ColorTheme.light

    Row(
        modifier = Modifier
            .border(menuTexture)
            .anchor {
                anchor = it
            }
            .clickable(interactionSource) {
                onExpandedChanged(!expanded)
            }
            .focusable(interactionSource)
            .then(modifier),
    ) {
        CompositionLocalProvider(
            LocalColorTheme provides colorTheme,
            LocalWidgetState provides state,
        ) {
            content()
        }
    }

    val currentAnchor = anchor
    if (expanded && currentAnchor != null) {
        val scope = SelectScopeImpl(currentAnchor, drawableSet)
        Popup(
            onDismissRequest = {
                onExpandedChanged(false)
            }
        ) {
            var screenSize by remember { mutableStateOf<IntSize?>(null) }
            var contentSize by remember { mutableStateOf(IntSize.ZERO) }
            val currentScreenSize = screenSize ?: IntSize.ZERO
            val anchorCenter = currentAnchor.offset + currentAnchor.size / 2
            val topSide = anchorCenter.top > currentScreenSize.height / 2
            val top = if (topSide) {
                currentAnchor.top - contentSize.height
            } else {
                currentAnchor.bottom
            }
            val left = if (currentAnchor.left + contentSize.width > currentScreenSize.width) {
                currentScreenSize.width - contentSize.width
            } else {
                currentAnchor.left
            }

            Layout(
                modifier = Modifier
                    .fillMaxSize()
                    .onPlaced { screenSize = it.size },
                measurePolicy = { measurables, _ ->
                    val constraints = Constraints()
                    val placeables = measurables.map { it.measure(constraints) }
                    val width = placeables.maxOfOrNull { it.width } ?: 0
                    val height = placeables.maxOfOrNull { it.height } ?: 0
                    layout(width, height) {
                        placeables.forEach { it.placeAt(left, top) }
                    }
                }
            ) {
                screenSize?.let { screenSize ->
                    Box(
                        modifier = Modifier
                            .border(drawableSet.floatPanel)
                            .minWidth(currentAnchor.size.width - 2)
                            .maxHeight(
                                if (topSide) {
                                    currentAnchor.top
                                } else {
                                    screenSize.height - currentAnchor.bottom
                                }
                            )
                            .onPlaced { contentSize = it.size }
                            .consumePress()
                    ) {
                        CompositionLocalProvider(
                            LocalColorTheme provides colorTheme
                        ) {
                            dropDownContent(scope)
                        }
                    }
                }
            }
        }
    }
}