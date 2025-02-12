package top.fifthlight.touchcontroller.control

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.component.inject
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.layout.Align
import top.fifthlight.touchcontroller.layout.Context
import top.fifthlight.touchcontroller.layout.PlayerListButton
import kotlin.math.round

@Serializable
enum class PlayerListButtonTexture {
    @SerialName("classic")
    CLASSIC,

    @SerialName("new")
    NEW,
}

@Serializable
@SerialName("player_list_button")
data class PlayerListButton(
    val size: Float = 1f,
    val texture: PlayerListButtonTexture = PlayerListButtonTexture.CLASSIC,
    override val align: Align = Align.CENTER_TOP,
    override val offset: IntOffset = IntOffset.ZERO,
    override val opacity: Float = 1f,
    override val lockMoving: Boolean = false,
) : ControllerWidget() {
    companion object {
        private val textFactory: TextFactory by inject()

        @Suppress("UNCHECKED_CAST")
        private val _properties = baseProperties + persistentListOf<Property<PlayerListButton, *>>(
            FloatProperty(
                getValue = { it.size },
                setValue = { config, value -> config.copy(size = value) },
                range = .5f..4f,
                messageFormatter = {
                    textFactory.format(
                        Texts.SCREEN_OPTIONS_WIDGET_PLAYER_LIST_BUTTON_PROPERTY_SIZE,
                        round(it * 100f).toString()
                    )
                }
            ),
            EnumProperty(
                getValue = { it.texture },
                setValue = { config, value -> config.copy(texture = value) },
                name = textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_PLAYER_LIST_BUTTON_PROPERTY_STYLE),
                items = listOf(
                    PlayerListButtonTexture.CLASSIC to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_PLAYER_LIST_BUTTON_PROPERTY_STYLE_CLASSIC),
                    PlayerListButtonTexture.NEW to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_PLAYER_LIST_BUTTON_PROPERTY_STYLE_NEW),
                ),
            ),
        ) as PersistentList<Property<ControllerWidget, *>>
    }

    override val properties
        get() = _properties

    private val textureSize
        get() = 18

    override fun size(): IntSize = IntSize((size * textureSize).toInt())

    override fun layout(context: Context) {
        context.PlayerListButton(this)
    }

    override fun cloneBase(
        align: Align,
        offset: IntOffset,
        opacity: Float,
        lockMoving: Boolean
    ) = copy(
        align = align,
        offset = offset,
        opacity = opacity,
        lockMoving = lockMoving,
    )
}