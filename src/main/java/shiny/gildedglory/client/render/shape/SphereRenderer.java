package shiny.gildedglory.client.render.shape;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.client.render.ModRenderLayers;

public class SphereRenderer {

    public static final Identifier TEXTURE = GildedGlory.id("textures/entity/beam.png");

    public static void render(MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, Vec3d pos, int light) {
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(ModRenderLayers.getSphere(TEXTURE));
        renderCube(matrices, vertexConsumer, pos, light);
    }

    public static void renderSphere(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d center, float radius, float sectorCount, float stackCount, int light) {
        float sectorStep = (float) (2 * Math.PI) / sectorCount;
        float stackStep = (float) Math.PI / stackCount;

        for (int i = 0; i <= stackCount; i++) {
            float stackAngle = (float) Math.PI / 2 - i * stackStep;

            float xy = radius * MathHelper.cos(stackAngle);
            float z = (float) center.z + radius * MathHelper.sin(stackAngle);

            for (int j = 0; j <= sectorCount; j++) {
                float sectorAngle = j * sectorStep;

                float x = (float) center.x + xy * MathHelper.cos(sectorAngle);
                float y = (float) center.y + xy * MathHelper.sin(sectorAngle);

                float nx = x * (1.0f / radius);
                float ny = y * (1.0f / radius);
                float nz = z * (1.0f / radius);

                float u = j / sectorCount;
                float v = j / stackCount;

                vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x, y, z)
                        .color(1.0f, 1.0f, 1.0f, 1.0f)
                        .texture(u, v)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(light)
                        .normal(matrices.peek().getNormalMatrix(), nx, ny, nz)
                        .next();
            }
        }
    }

    public static void renderCube(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d pos, int light) {
        for (Vec3d vertex : generateCubeVertices(8)) {
            vertex = pos.add(vertex);
            vertexConsumer.vertex(matrices.peek().getPositionMatrix(), (float) vertex.x, (float) vertex.y, (float) vertex.z)
                    .color(1.0f, 1.0f, 1.0f, 1.0f)
                    .texture(1, 1)
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(light)
                    .normal(matrices.peek().getNormalMatrix(), 0 , 1, 0)
                    .next();
        }
    }

    public static Vec3d[] generateCubeVertices(int resolution) {
        Vec3d axisX = new Vec3d(1, 0,0);
        Vec3d axisY = new Vec3d(0, 1, 0);
        Vec3d axisZ = new Vec3d(0, 0, 1);

        Vec3d[] vertices = new Vec3d[resolution * resolution];

        //This method generates vertices along each x step for each y step of the current cube face
        //The issue with this is that the vertices don't match into triangles but rather lines
//        for (int i = 0; i < resolution; i++) {
//            for (int j = 0; j < resolution; j++) {
//                Vector2d percent = new Vector2d((double) j / (resolution - 1), (double) i / (resolution - 1));
//
//                double x = axisY.x + (percent.x - 0.5) * 2 * axisX.x + (percent.y - 0.5) * 2 * axisZ.x;
//                double y = axisY.y + (percent.x - 0.5) * 2 * axisX.y + (percent.y - 0.5) * 2 * axisZ.y;
//                double z = axisY.z + (percent.x - 0.5) * 2 * axisX.z + (percent.y - 0.5) * 2 * axisZ.z;
//
//                Vec3d vertex = new Vec3d(x, y, z);
//                vertices[i + j * resolution] = vertex;
//            }
//        }


        for (int i = 0; i < resolution - 1; i++) {
            for (int j = 0; j < resolution; j++) {
                double percentX = (double) j / (resolution - 1.0);
                double percentY = (double) i / (resolution - 1.0);
                double nextPercentY = (double) i + 1.0 / (resolution - 1.0);

                double x = axisY.x + (percentX - 0.5) * 2 * axisX.x + (percentY - 0.5) * 2 * axisZ.x;
                double y = axisY.y + (percentX - 0.5) * 2 * axisX.y + (percentY - 0.5) * 2 * axisZ.y;
                double z = axisY.z + (percentX - 0.5) * 2 * axisX.z + (percentY - 0.5) * 2 * axisZ.z;

                double nextX = axisY.x + (percentX - 0.5) * 2 * axisX.x + (nextPercentY - 0.5) * 2 * axisZ.x;
                double nextY = axisY.y + (percentX - 0.5) * 2 * axisX.y + (nextPercentY - 0.5) * 2 * axisZ.y;
                double nextZ = axisY.z + (percentX - 0.5) * 2 * axisX.z + (nextPercentY - 0.5) * 2 * axisZ.z;

                vertices[i + j * resolution] = new Vec3d(x, y, z);
                vertices[i + j * resolution + 1] = new Vec3d(nextX,nextY, nextZ);

//                if (i < resolution - 1) {
//                    vertices[i + j * resolution + 1] = new Vec3d(nextX,nextY, nextZ);
//                }
            }
        }


        return vertices;
    }

    //Correct steps should be (0, 0) is top left, (1, 1) is bottom right, resolution of 4
    // (0, 0)
    // (1, 0)
    // (1, 1) - first triangle done
    // (0, 1) - second triangle done, first quad done
    // (1, 2) - third triangle done
    // (0, 2) - fourth triangle done, second quad done
    // (1, 3) - fifth triangle done
    // (0, 3) - sixth triangle done, third quad done
    // (1, 4) - seventh triangle done
    // (0, 4) - eighth triangle done, fourth quad done

}
