package shiny.gildedglory.client.particle.effect;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import org.joml.Vector3f;

import java.util.Locale;

public class ColoredEntityParticleEffect implements ParticleEffect {

    public static final ParticleEffect.Factory<ColoredEntityParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<>() {
        public ColoredEntityParticleEffect read(ParticleType<ColoredEntityParticleEffect> type, StringReader reader) throws CommandSyntaxException {
            Vector3f color = VectorParticleEffect.readColor(reader);
            reader.expect(' ');
            float scale = reader.readFloat();
            reader.expect(' ');
            int duration = reader.readInt();
            reader.expect(' ');
            int entityId = reader.readInt();
            return new ColoredEntityParticleEffect(type, color, entityId, scale, duration);
        }
        public ColoredEntityParticleEffect read(ParticleType<ColoredEntityParticleEffect> type, PacketByteBuf buf) {
            return new ColoredEntityParticleEffect(type, VectorParticleEffect.readColor(buf), buf.readInt(), buf.readFloat(), buf.readInt());
        }
    };

    private final ParticleType<ColoredEntityParticleEffect> type;
    private final Vector3f color;
    private final float scale;
    private final int duration;
    private final int entityId;

    public ColoredEntityParticleEffect(ParticleType<ColoredEntityParticleEffect> type, Vector3f color, int entityId, float scale, int duration) {
        this.type = type;
        this.color = color;
        this.entityId = entityId;
        this.scale = scale;
        this.duration = duration;
    }

    public Vector3f getColor() {
        return this.color;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public float getScale() {
        return this.scale;
    }

    public int getDuration() {
        return this.duration;
    }

    @Override
    public ParticleType<ColoredEntityParticleEffect> getType() {
        return this.type;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeFloat(this.color.x());
        buf.writeFloat(this.color.y());
        buf.writeFloat(this.color.z());
        buf.writeFloat(this.scale);
        buf.writeInt(this.duration);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT,
                "%s %.2f %.2f %.2f %d %.2f %d",
                Registries.PARTICLE_TYPE.getId(this.type),
                this.color.x,
                this.color.y,
                this.color.z,
                this.entityId,
                this.scale,
                this.duration
        );
    }
}
