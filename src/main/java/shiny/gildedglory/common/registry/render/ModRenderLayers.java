package shiny.gildedglory.common.registry.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.BiFunction;

public class ModRenderLayers extends RenderPhase {

    public static final BiFunction<Identifier, Boolean, RenderLayer> BEAM = Util.memoize(
            (texture, affectsOutline) -> {
                RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
                        .program(BEACON_BEAM_PROGRAM)
                        .texture(new Texture(texture, false, false))
                        .transparency(affectsOutline ? TRANSLUCENT_TRANSPARENCY : LIGHTNING_TRANSPARENCY)
                        .writeMaskState(affectsOutline ? COLOR_MASK : ALL_MASK)
                        .cull(DISABLE_CULLING)
                        .build(false);
                return RenderLayer.of("beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, multiPhaseParameters);
            }
    );
    public static final BiFunction<Identifier, Boolean, RenderLayer> SLASH = Util.memoize(
            (texture, affectsOutline) -> {
                RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
                        .program(RenderPhase.ENTITY_TRANSLUCENT_CULL_PROGRAM)
                        .texture(new Texture(texture, false, false))
                        .transparency(RenderPhase.LIGHTNING_TRANSPARENCY)
                        .cull(RenderPhase.DISABLE_CULLING)
                        .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                        .build(false);
                return RenderLayer.of("slash", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, multiPhaseParameters);
            }
    );
    public static final BiFunction<Identifier, Boolean, RenderLayer> SPHERE_TRANSLUCENT = Util.memoize(
            (texture, affectsOutline) -> {
                RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
                        .program(ENTITY_TRANSLUCENT_PROGRAM)
                        .texture(new Texture(texture, false, false))
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .cull(DISABLE_CULLING)
                        .lightmap(ENABLE_LIGHTMAP)
                        .overlay(ENABLE_OVERLAY_COLOR)
                        .build(affectsOutline);
                return RenderLayer.of(
                        "sphere_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.TRIANGLE_STRIP, 256, true, true, multiPhaseParameters
                );
            }
    );

    public ModRenderLayers(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    public static RenderLayer getBeam(Identifier texture, boolean inner) {
        return BEAM.apply(texture, inner);
    }

    public static RenderLayer getSlash(Identifier texture) {
        return SLASH.apply(texture, false);
    }

    public static RenderLayer getSphere(Identifier texture) {
        return SPHERE_TRANSLUCENT.apply(texture, false);
    }
}
