package shiny.gildedglory.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import shiny.gildedglory.GildedGlory;

public class ModShaderPrograms {

    public static ShaderProgram mirror;

    public static void registerModShaderPrograms() {
        CoreShaderRegistrationCallback.EVENT.register(ctx -> {
            ctx.register(GildedGlory.id("rendertype_mirror"), VertexFormats.POSITION, program -> mirror = program);
        });
    }

    public static ShaderProgram getMirror() {
        return mirror;
    }
}
