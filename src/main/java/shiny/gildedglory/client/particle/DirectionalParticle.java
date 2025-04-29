package shiny.gildedglory.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import shiny.gildedglory.client.particle.effect.VectorParticleEffect;

public class DirectionalParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;
    private final Vec3d direction;

    public DirectionalParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, VectorParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.spriteProvider = spriteProvider;

        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;

        this.direction = new Vec3d(parameters.getVector());
        this.scale = parameters.getScale();
        this.maxAge = parameters.getDuration();

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d vec3 = camera.getPos();

        float yRot = ((float) (MathHelper.atan2(this.direction.x, this.direction.z) * (double) (180f / (float) Math.PI)));
        float xRot = ((float) (MathHelper.atan2(this.direction.y, this.direction.horizontalLength()) * (double) (180f / (float) Math.PI)));
        float yaw = (float) Math.toRadians(yRot);
        float pitch = (float) Math.toRadians(-xRot);

        float x = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3.getX());
        float y = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3.getY());
        float z = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3.getZ());

        float f = this.getSize(tickDelta);

        Quaternionf quaternion = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
        quaternion.mul(this.rotate(0, yaw, 0));
        quaternion.mul(this.rotate(pitch, 0, 0));

        if (this.angle != 0) {
            quaternion.rotateZ(MathHelper.lerp(tickDelta, this.prevAngle, this.angle));
        }

        Vector3f[] vecUp = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        Vector3f[] vecDown = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, -1.0f, 0.0f)};

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = vecUp[i];
            vector3f.rotate(quaternion);
            vector3f.mul(f);
            vector3f.add(x, y, z);

            Vector3f vector3fBottom = vecDown[i];
            vector3fBottom.rotate(quaternion);
            vector3fBottom.mul(f);
            vector3fBottom.add(x, y - 0.1f, z);
        }

        float g = this.getMinU();
        float h = this.getMaxU();
        float i = this.getMinV();
        float j = this.getMaxV();
        int light = this.getBrightness(tickDelta);

        //Top quad
        vertexConsumer.vertex(vecUp[0].x(), vecUp[0].y(), vecUp[0].z()).texture(h, j).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(vecUp[1].x(), vecUp[1].y(), vecUp[1].z()).texture(h, i).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(vecUp[2].x(), vecUp[2].y(), vecUp[2].z()).texture(g, i).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(vecUp[3].x(), vecUp[3].y(), vecUp[3].z()).texture(g, j).color(this.red, this.green, this.blue, this.alpha).light(light).next();

        //Bottom quad
        vertexConsumer.vertex(vecUp[3].x(), vecUp[3].y(), vecUp[3].z()).texture(g, j).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(vecUp[2].x(), vecUp[2].y(), vecUp[2].z()).texture(g, i).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(vecUp[1].x(), vecUp[1].y(), vecUp[1].z()).texture(h, i).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(vecUp[0].x(), vecUp[0].y(), vecUp[0].z()).texture(h, j).color(this.red, this.green, this.blue, this.alpha).light(light).next();
    }

    public Quaternionf rotate(float pX, float pY, float pZ) {
        float f = MathHelper.sin(0.5f * pX);
        float g = MathHelper.cos(0.5f * pX);
        float h = MathHelper.sin(0.5f * pY);
        float i = MathHelper.cos(0.5f * pY);
        float j = MathHelper.sin(0.5f * pZ);
        float k = MathHelper.cos(0.5f * pZ);

        float x = f * i * k + g * h * j;
        float y = g * h * k - f * i * j;
        float z = f * h * k + g * i * j;
        float w = g * i * k - f * h * j;

        return new Quaternionf(x, y, z, w);
    }

    @Override
    protected int getBrightness(float tint) {
        return 15728880;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<VectorParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(VectorParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new DirectionalParticle(clientWorld, d, e, f, g, h, i, parameters, this.spriteProvider);
        }
    }
}
