package top.fifthlight.combine.backend.minecraft_1_21_8.extension;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

public interface SubmittableGuiGraphics {
    void touchcontroller$submitElement(GuiElementRenderState guiElementRenderState);
    ScreenRectangle touchcontroller$peekScissorStack();
}
