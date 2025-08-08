package shiny.gildedglory.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.BiFunction;

public class ModRenderLayers extends RenderLayer {

    //Shader Programs
    public static final RenderPhase.ShaderProgram MIRROR_PROGRAM = new ShaderProgram(ModShaderPrograms::getMirror);

    //RenderLayers
    private static final BiFunction<Identifier, Boolean, RenderLayer> BEAM = Util.memoize(
            (texture, affectsOutline) -> {
                MultiPhaseParameters multiPhaseParameters = MultiPhaseParameters.builder()
                        .program(BEACON_BEAM_PROGRAM)
                        .texture(new Texture(texture, false, false))
                        .transparency(affectsOutline ? TRANSLUCENT_TRANSPARENCY : LIGHTNING_TRANSPARENCY)
                        .writeMaskState(affectsOutline ? COLOR_MASK : ALL_MASK)
                        .cull(DISABLE_CULLING)
                        .build(false);
                return of("gildedglory:beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, multiPhaseParameters);
            }
    );
    private static final BiFunction<Identifier, Boolean, RenderLayer> SLASH = Util.memoize(
            (texture, affectsOutline) -> {
                MultiPhaseParameters multiPhaseParameters = MultiPhaseParameters.builder()
                        .program(ENTITY_TRANSLUCENT_CULL_PROGRAM)
                        .texture(new Texture(texture, false, false))
                        .transparency(LIGHTNING_TRANSPARENCY)
                        .cull(DISABLE_CULLING)
                        .lightmap(ENABLE_LIGHTMAP)
                        .build(false);
                return of("gildedglory:slash", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, multiPhaseParameters);
            }
    );
    private static final BiFunction<Identifier, Boolean, RenderLayer> SPHERE_TRANSLUCENT = Util.memoize(
            (texture, affectsOutline) -> {
                MultiPhaseParameters multiPhaseParameters = MultiPhaseParameters.builder()
                        .program(ENTITY_TRANSLUCENT_PROGRAM)
                        .texture(new Texture(texture, false, false))
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .cull(DISABLE_CULLING)
                        .lightmap(ENABLE_LIGHTMAP)
                        .overlay(ENABLE_OVERLAY_COLOR)
                        .build(affectsOutline);
                return of(
                        "gildedglory:sphere_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.TRIANGLE_STRIP, 256, true, true, multiPhaseParameters
                );
            }
    );
    private static final RenderLayer SLASHED_AREA = of(
            "gildedglory:slashed_area",
            VertexFormats.POSITION_COLOR,
            VertexFormat.DrawMode.QUADS,
            256,
            false, true,
            MultiPhaseParameters.builder()
                    .program(COLOR_PROGRAM)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .writeMaskState(ALL_MASK)
                    .cull(DISABLE_CULLING)
                    .build(false)
    );
    private static final RenderLayer MIRROR = of(
            "gildedglory:mirror",
            VertexFormats.POSITION,
            VertexFormat.DrawMode.QUADS,
            256,
            false, true,
            MultiPhaseParameters.builder()
                    .lightmap(DISABLE_LIGHTMAP)
                    .program(MIRROR_PROGRAM)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .depthTest(ALWAYS_DEPTH_TEST)
                    .target(ModRenderPhase.MIRROR_TARGET)
                    .build(false)
    );

    public ModRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
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

    public static RenderLayer getSlashedArea() {
        return SLASHED_AREA;
    }

    public static RenderLayer getMirror() {
        return MIRROR;
    }
}
