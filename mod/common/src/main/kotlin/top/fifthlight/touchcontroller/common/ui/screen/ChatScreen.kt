package top.fifthlight.touchcontroller.common.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.background
import top.fifthlight.combine.modifier.placement.fillMaxHeight
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.height
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.screen.ScreenFactory
import top.fifthlight.combine.util.LocalCloseHandler
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.ui.EditText
import top.fifthlight.combine.widget.ui.Icon
import top.fifthlight.combine.widget.ui.IconButton
import top.fifthlight.combine.widget.ui.Text
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.common.ui.component.AppBar
import top.fifthlight.touchcontroller.common.ui.component.BackButton
import top.fifthlight.touchcontroller.common.ui.component.Scaffold
import top.fifthlight.touchcontroller.common.ui.model.ChatScreenModel

@Composable
private fun ChatScreen() {
    val closeHandler = LocalCloseHandler.current
    val screenModel: ChatScreenModel = koinInject()
    DisposableEffect(screenModel) {
        onDispose {
            screenModel.onDispose()
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                modifier = Modifier.fillMaxWidth(),
                leading = {
                    BackButton(
                        screenName = Text.translatable(Texts.SCREEN_CHAT_EXIT),
                    )
                },
                title = {
                    Text(Text.translatable(Texts.SCREEN_CHAT_TITLE))
                },
            )
        },
    ) { modifier ->
        Column(
            modifier = modifier,
        ) {
            Box(
                modifier = Modifier
                    .background(Colors.TRANSPARENT_BLACK)
                    .weight(1f)
                    .fillMaxWidth(),
                alignment = Alignment.Center,
            ) {
                Text("TODO")
            }
            val bottomBarHeight = 32
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomBarHeight),
            ) {
                IconButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(bottomBarHeight),
                    onClick = {}
                ) {
                    Icon(Textures.ICON_CHAT_KEYBOARD)
                }
                IconButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(bottomBarHeight),
                    onClick = {}
                ) {
                    Icon(Textures.ICON_CHAT_COMMAND)
                }
                IconButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(bottomBarHeight),
                    onClick = {}
                ) {
                    Icon(Textures.ICON_CHAT_SETTING)
                }
                EditText(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    value = "TODO",
                    onValueChanged = {}
                )
                IconButton(
                    modifier = Modifier
                        .width(64)
                        .fillMaxHeight(),
                    onClick = {}
                ) {
                    Icon(Textures.ICON_CHAT_SEND)
                }
            }
        }
    }
}

fun openChatScreen(parent: Any? = null): Any? = with(GlobalContext.get()) {
    val textFactory: TextFactory = get()
    val screenFactory: ScreenFactory = get()
    screenFactory.openScreen(
        renderBackground = false,
        title = textFactory.of(Texts.SCREEN_CHAT_TITLE),
    ) {
        ChatScreen()
    }
}
