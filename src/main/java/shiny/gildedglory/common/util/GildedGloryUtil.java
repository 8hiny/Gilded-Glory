package shiny.gildedglory.common.util;

import me.pepperbell.simplenetworking.S2CPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import shiny.gildedglory.client.sound.DynamicSounds;
import shiny.gildedglory.common.network.ChargingParticleS2CPacket;
import shiny.gildedglory.common.network.ItemUseSoundS2CPacket;
import shiny.gildedglory.common.network.ModPackets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class GildedGloryUtil {

    public static float random(float min, float max) {
        return (float) Math.random() * (max - min) + min;
    }

    public static double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public static float closerTo(float value, float thisFloat, float thatFloat) {
        return Math.abs(value - thisFloat) < Math.abs(value - thatFloat) ? thisFloat : thatFloat;
    }

    public static Vec3d getThrowPos(Entity thrower, EntityType<?> entityType) {
        return thrower.getEyePos().subtract(0, entityType.getHeight() / 2, 0);
    }

    /**
     * Limits a vector such that it does not point more than a certain amount away from a position.
     * If the vector's origin position is outside the allowed range, the vector points towards the target position, multiplied by the distance to it.
     * @param originPos The position from which the velocity originates
     * @param targetPos The position which is compared to
     * @param distance The radius from targetPos wherein the velocity may remain unchanged
     */
    public static Vec3d adjustVelocity(Vec3d originPos, Vec3d targetPos, Vec3d velocity, double distance) {
        Vec3d nextPos = originPos.add(velocity);
        double nextDist = nextPos.distanceTo(targetPos);

        if (nextDist > distance) {
            Vec3d direction = nextPos.subtract(targetPos).normalize();
            Vec3d boundary = targetPos.add(direction.multiply(distance));

            velocity = boundary.subtract(originPos);
            if (nextDist - distance > 3.0) velocity = velocity.multiply(1 / (nextDist - distance)).normalize();
        }
        return velocity;
    }

    public static Entity getEntityFromUuid(UUID uuid, World world) {
        Entity counterpart;

        if (world.isClient()) {
            counterpart = GildedGloryUtil.getEntityClient(uuid, (ClientWorld) world);
        }
        else {
            counterpart = ((ServerWorld) world).getEntity(uuid);
        }
        return counterpart;
    }

    public static Entity getEntityClient(UUID uuid, ClientWorld world) {
        for (Entity entity : world.getEntities()) {
            if (entity.getUuid().equals(uuid)) return entity;
        }
        return null;
    }

    /**
     * Plays a looping sound from a living entity.
     * @param user The entity from which the sound is played
     * @param sound The looping sound to be played
     */
    public static void playLoopingSound(World world, Entity user, Identifier sound) {
        if (world.isClient() && user instanceof PlayerEntity) {
            DynamicSoundManager.getInstance().play(DynamicSounds.get(sound, (DynamicSoundSource) user));
        }
        else {
            sendSoundPackets(world, user, user, sound);
        }
    }

    /**
     * Plays a looping sound to nearby players.
     * @param user The entity from which the sound is played
     * @param sound The looping sound to be played
     */
    public static void sendSoundPackets(World world, Entity user, Entity exclude, Identifier sound) {
        ItemUseSoundS2CPacket packet = new ItemUseSoundS2CPacket(user.getId(), sound);
        sendPackets(packet, world, user, exclude);
    }

    /**
     * Displays square charging particles to nearby players.
     * @param color The color of the particle in rgb
     * @param dx The velocity of the particle on the x axis
     * @param dy The velocity of the particle on the y axis
     * @param dz The velocity of the particle on the z axis
     */
    public static void sendChargingParticlePackets(World world, Entity source, Vector3f color, float dx, float dy, float dz) {
        ChargingParticleS2CPacket packet = new ChargingParticleS2CPacket(
                source.getId(),
                color.x,
                color.y,
                color.z,
                (float) source.getParticleX(1.0),
                (float) source.getRandomBodyY(),
                (float) source.getParticleZ(1.0),
                dx, dy, dz
        );
        sendPackets(packet, world, source, null);
    }

    /**
     * Sends packets from an entity to any players currently tracking that entity.
     * @param exclude The entity to exclude from receiving the packet if it is a player
     */
    public static <T extends S2CPacket> void sendPackets(T packet, World world, Entity sender, @Nullable Entity exclude) {
        if (!world.isClient()) {
            for (ServerPlayerEntity player : PlayerLookup.tracking(sender)) {
                if (player != exclude) ModPackets.GILDED_GLORY_CHANNEL.sendToClient(packet, player);
            }
            if (sender != exclude && sender instanceof ServerPlayerEntity player) {
                ModPackets.GILDED_GLORY_CHANNEL.sendToClient(packet, player);
            }
        }
    }

    /**
     * Raycasts forward within a set distance and returns a list of all entities hit by the ray.
     * @param origin The entity from which to cast the ray
     * @param predicate The predicate which hit entities must fulfill
     * @param direction The direction of the ray
     * @param margin The leniency of detection
     * @param distance The range of the ray
     * @param collision Whether the ray should stop at blocks
     */
    public static List<LivingEntity> raycast(Entity origin, Predicate<LivingEntity> predicate, Vec3d direction, float margin, float distance, boolean collision) {
        List<LivingEntity> entities = new ArrayList<>();
        World world = origin.getWorld();

        Vec3d pos = origin.getEyePos();
        Vec3d ray = direction.multiply(distance);
        Vec3d max = pos.add(ray);
        Box range = origin.getBoundingBox().stretch(ray).expand(1.0);

        double d = max.squaredDistanceTo(pos);
        if (collision) {
            RaycastContext context = new RaycastContext(pos, pos.add(ray), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, origin);
            BlockHitResult hitResult = world.raycast(context);

            if (hitResult.getType() != HitResult.Type.MISS) {
                d = hitResult.squaredDistanceTo(origin);
            }
        }

        for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, range, predicate)) {
            if (!entities.contains(entity)) {
                Box box = entity.getBoundingBox().expand(entity.getTargetingMargin() + margin);
                Optional<Vec3d> optional = box.raycast(pos, max);

                if (optional.isPresent()) {
                    double e = optional.get().squaredDistanceTo(pos);
                    if (d > e) entities.add(entity);
                }
            }
        }
        return entities;
    }

    public static LivingEntity raycastSingle(Entity origin, Predicate<LivingEntity> predicate, Vec3d direction, float margin, float distance, boolean collision) {
        for (LivingEntity entity : raycast(origin, predicate, direction, margin, distance, collision)) {
            return entity;
        }
        return null;
    }
}
