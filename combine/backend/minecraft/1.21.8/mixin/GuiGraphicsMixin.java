package top.fifthlight.combine.backend.minecraft_1_21_8.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.fifthlight.combine.backend.minecraft_1_21_8.extension.SubmittableGuiGraphics;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements SubmittableGuiGraphics {
    @Shadow @Final private GuiRenderState guiRenderState;

    @Shadow @Final private ScissorStackInvoker scissorStack;

    @Mixin(targets = "net/minecraft/client/gui/GuiGraphics$ScissorStack")
    private interface ScissorStackInvoker {
        @Invoker("peek") ScreenRectangle touchcontroller$callPeek();
    }

    @ModifyArg(method = "<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/render/"
            + "state/GuiRenderState;)V",
        at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;<init>(I)V", ordinal = 0))
    private static int
    modifyStackLimit(int stackSize) {
        return Math.max(stackSize, 64);
    }

    @Override
    public void touchcontroller$submitElement(GuiElementRenderState guiElementRenderState) {
        guiRenderState.submitGuiElement(guiElementRenderState);
    }

    @Override
    public ScreenRectangle touchcontroller$peekScissorStack() {
        return scissorStack.touchcontroller$callPeek();
    }
}
