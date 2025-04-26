package shiny.gildedglory.common.registry.render;

import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.BiFunction;

public class ModRenderLayers extends RenderPhase {

    private static final BiFunction<Identifier, Boolean, net.minecraft.client.render.RenderLayer> BEAM = Util.memoize(
            (texture, affectsOutline) -> {
                net.minecraft.client.render.RenderLayer.MultiPhaseParameters multiPhaseParameters = net.minecraft.client.render.RenderLayer.MultiPhaseParameters.builder()
                        .program(BEACON_BEAM_PROGRAM)
                        .texture(new Texture(texture, false, false))
                        .transparency(affectsOutline ? TRANSLUCENT_TRANSPARENCY : LIGHTNING_TRANSPARENCY)
                        .writeMaskState(affectsOutline ? COLOR_MASK : ALL_MASK)
                        .cull(DISABLE_CULLING)
                        .build(false);
                return net.minecraft.client.render.RenderLayer.of("beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, multiPhaseParameters);
            }
    );
    private static final BiFunction<Identifier, Boolean, net.minecraft.client.render.RenderLayer> SLASH = Util.memoize(
            (texture, affectsOutline) -> {
                net.minecraft.client.render.RenderLayer.MultiPhaseParameters multiPhaseParameters = net.minecraft.client.render.RenderLayer.MultiPhaseParameters.builder()
                        .program(RenderPhase.ENTITY_TRANSLUCENT_CULL_PROGRAM)
                        .texture(new Texture(texture, false, false))
                        .transparency(RenderPhase.LIGHTNING_TRANSPARENCY)
                        .cull(RenderPhase.DISABLE_CULLING)
                        .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                        .build(false);
                return net.minecraft.client.render.RenderLayer.of("slash", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, multiPhaseParameters);
            }
    );

    public ModRenderLayers(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    public static net.minecraft.client.render.RenderLayer getBeam(Identifier texture, boolean inner) {
        return BEAM.apply(texture, inner);
    }

    public static net.minecraft.client.render.RenderLayer getSlash(Identifier texture) {
        return SLASH.apply(texture, false);
    }
}
