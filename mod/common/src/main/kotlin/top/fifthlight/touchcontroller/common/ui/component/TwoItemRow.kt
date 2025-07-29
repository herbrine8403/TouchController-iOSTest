package top.fifthlight.touchcontroller.common.ui.component

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.layout.offset
import top.fifthlight.combine.modifier.Modifier

@Composable
fun TwoItemRow(
    modifier: Modifier = Modifier,
    rightWidth: Int,
    space: Int = 0,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            require(measurables.size == 2) { "TwoItemRow must contain two items" }

            val (left, right) = measurables
            val leftPlaceable = left.measure(
                constraints
                    .offset(-rightWidth - space, 0)
                    .copy(minHeight = rightWidth, maxHeight = rightWidth)
            )
            val rightPlaceable = right.measure(
                constraints.copy(
                    minWidth = rightWidth,
                    maxWidth = rightWidth,
                    minHeight = leftPlaceable.height,
                    maxHeight = leftPlaceable.height,
                )
            )

            layout(leftPlaceable.width + rightPlaceable.width, leftPlaceable.height) {
                leftPlaceable.placeAt(0, 0)
                rightPlaceable.placeAt(leftPlaceable.width + space, 0)
            }
        },
        content = content,
    )
}