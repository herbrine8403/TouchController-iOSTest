package top.fifthlight.touchcontroller.ui.component

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.util.LocalCloseHandler
import top.fifthlight.combine.widget.ui.Text
import top.fifthlight.combine.widget.ui.TextButton
import top.fifthlight.touchcontroller.assets.Texts

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    screenName: Text,
    close: Boolean = false,
) {
    val closeHandler = LocalCloseHandler.current
    val navigator = LocalNavigator.current
    TextButton(
        modifier = modifier,
        onClick = {
            if (close) {
                closeHandler.close()
            } else {
                navigator?.pop()
            }
        }
    ) {
        Text(Text.format(Texts.BACK, screenName.string))
    }
}