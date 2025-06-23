package top.fifthlight.touchcontroller.common.ui.screen

import androidx.compose.runtime.*
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.combine.input.MutableInteractionSource
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.background
import top.fifthlight.combine.modifier.focus.FocusInteraction
import top.fifthlight.combine.modifier.focus.FocusRequester
import top.fifthlight.combine.modifier.focus.focusRequester
import top.fifthlight.combine.modifier.placement.fillMaxHeight
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.height
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.screen.ScreenFactory
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.ui.EditText
import top.fifthlight.combine.widget.ui.Icon
import top.fifthlight.combine.widget.ui.IconButton
import top.fifthlight.combine.widget.ui.Text
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.common.gal.ChatMessageProvider
import top.fifthlight.touchcontroller.common.ui.component.AppBar
import top.fifthlight.touchcontroller.common.ui.component.BackButton
import top.fifthlight.touchcontroller.common.ui.component.Scaffold
import top.fifthlight.touchcontroller.common.ui.model.ChatScreenModel

@Composable
private fun ChatScreen() {
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
        val uiState by screenModel.uiState.collectAsState()
        Column(
            modifier = modifier,
        ) {
            val messageProvider: ChatMessageProvider = koinInject()
            var messages by remember { mutableStateOf(messageProvider.getMessages()) }
            LaunchedEffect(Unit) {
                while (true) {
                    withFrameMillis { delta ->
                        messages = messageProvider.getMessages()
                    }
                }
            }
            Column(
                modifier = Modifier
                    .verticalScroll(true)
                    .background(Colors.TRANSPARENT_BLACK)
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
            ) {
                for (message in messages) {
                    Text(message.message)
                }
            }
            val bottomBarHeight = 32
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomBarHeight),
            ) {
                val focusRequester = remember { FocusRequester() }
                val interactionSource = remember { MutableInteractionSource() }
                var focused by remember { mutableStateOf(false) }
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        when (it) {
                            FocusInteraction.Blur -> {
                                focused = false
                            }

                            FocusInteraction.Focus -> {
                                focused = true
                            }
                        }
                    }
                }
                IconButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(bottomBarHeight),
                    focusable = false,
                    onClick = {
                        if (focused) {
                            focusRequester.requestBlur()
                        } else {
                            focusRequester.requestFocus()
                        }
                    },
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
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .weight(1f)
                        .fillMaxHeight(),
                    value = uiState.text,
                    onValueChanged = screenModel::updateText,
                )
                IconButton(
                    modifier = Modifier
                        .width(64)
                        .fillMaxHeight(),
                    onClick = screenModel::sendText,
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
