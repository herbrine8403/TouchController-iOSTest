package top.fifthlight.armorstand

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.state.PlayerRenderState
import org.joml.Matrix4f
import top.fifthlight.armorstand.config.ConfigHolder
import top.fifthlight.armorstand.state.ModelInstanceManager
import top.fifthlight.armorstand.util.RendererManager
import top.fifthlight.blazerod.api.render.ScheduledRenderer
import top.fifthlight.blazerod.api.resource.CameraTransform
import top.fifthlight.blazerod.model.Camera
import java.lang.ref.WeakReference
import java.util.*

object PlayerRenderer {
    private var renderingWorld = false

    private var prevModelItem = WeakReference<ModelInstanceManager.ModelInstanceItem.Model?>(null)
    val selectedCameraIndex = MutableStateFlow<Int?>(null)
    private val _totalCameras = MutableStateFlow<List<Camera>?>(listOf())
    val totalCameras = _totalCameras.asStateFlow()
    private var cameraTransform: CameraTransform? = null

    @JvmStatic
    fun getCurrentCameraTransform(): CameraTransform? {
        cameraTransform?.let { return it }
        val entry = ModelInstanceManager.getSelfItem(load = false) ?: return null
        if (prevModelItem.get() != entry) {
            selectedCameraIndex.value = null
            if (entry is ModelInstanceManager.ModelInstanceItem.Model) {
                _totalCameras.value = entry.instance.scene.cameras
                prevModelItem = WeakReference(entry)
            } else {
                _totalCameras.value = listOf()
            }
            return null
        }

        val selectedIndex = selectedCameraIndex.value ?: return null
        val instance = entry.instance
        instance.updateCamera()

        return instance.getCameraTransform(selectedIndex).also {
            cameraTransform = it
        } ?: run {
            selectedCameraIndex.value = null
            null
        }
    }

    fun startRenderWorld() {
        renderingWorld = true
    }

    private val matrix = Matrix4f()

    @JvmStatic
    fun updatePlayer(
        player: AbstractClientPlayer,
        state: PlayerRenderState,
    ) {
        val uuid = player.uuid
        val entry = ModelInstanceManager.get(uuid, System.nanoTime())
        if (entry !is ModelInstanceManager.ModelInstanceItem.Model) {
            return
        }

        val controller = entry.controller
        controller.update(uuid, player, state)
    }

    @JvmStatic
    fun appendPlayer(
        uuid: UUID,
        vanillaState: PlayerRenderState,
        matrixStack: PoseStack,
        consumers: MultiBufferSource,
        light: Int,
        overlay: Int,
    ): Boolean {
        val entry = ModelInstanceManager.get(uuid, System.nanoTime())
        if (entry !is ModelInstanceManager.ModelInstanceItem.Model) {
            return false
        }

        val controller = entry.controller
        val instance = entry.instance

        controller.apply(uuid, instance, vanillaState)
        instance.updateRenderData()

        val backupItem = matrixStack.peek().copy()
        matrixStack.pop()
        matrixStack.push()

        if (ArmorStandClient.instance.debugBone) {
            instance.debugRender(matrixStack.peek().positionMatrix, consumers)
        } else {
            matrix.set(matrixStack.peek().positionMatrix)
            matrix.scale(ConfigHolder.config.value.modelScale)
            val currentRenderer = RendererManager.currentRenderer
            val task = instance.createRenderTask(matrix, light, overlay)
            if (currentRenderer is ScheduledRenderer<*, *> && renderingWorld) {
                currentRenderer.schedule(task)
            } else {
                val mainTarget = MinecraftClient.getInstance().framebuffer
                val colorFrameBuffer = RenderSystem.outputColorTextureOverride ?: mainTarget.colorAttachmentView!!
                val depthFrameBuffer = RenderSystem.outputDepthTextureOverride ?: mainTarget.depthAttachmentView
                currentRenderer.render(
                    colorFrameBuffer = colorFrameBuffer,
                    depthFrameBuffer = depthFrameBuffer,
                    scene = instance.scene,
                    task = task,
                )
                task.release()
            }
        }

        matrixStack.pop()
        matrixStack.push()
        matrixStack.peek().apply {
            positionMatrix.set(backupItem.positionMatrix)
            normalMatrix.set(backupItem.normalMatrix)
        }
        return true
    }

    fun executeDraw() {
        renderingWorld = false
        val mainTarget = MinecraftClient.getInstance().framebuffer
        RendererManager.currentRendererScheduled?.let { renderer ->
            val colorFrameBuffer = RenderSystem.outputColorTextureOverride ?: mainTarget.colorAttachmentView!!
            val depthFrameBuffer = RenderSystem.outputDepthTextureOverride ?: mainTarget.depthAttachmentView
            renderer.executeTasks(colorFrameBuffer, depthFrameBuffer)
        }
    }

    fun endFrame() {
        cameraTransform = null
    }
}
