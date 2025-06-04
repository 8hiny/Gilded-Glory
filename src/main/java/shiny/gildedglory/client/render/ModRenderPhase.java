package shiny.gildedglory.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderPhase;

public class ModRenderPhase extends RenderPhase {

    public static final Target MIRROR_TARGET = new Target(
            "gildedglory:mirror_target",
            () -> ModShaders.getInstance().getMirrorFramebuffer().beginWrite(false),
            () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false)
    );

    public ModRenderPhase(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }
}
