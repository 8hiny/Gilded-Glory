package shiny.gildedglory.client.particle.effect;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.*;
import net.minecraft.registry.Registries;
import org.joml.Vector3f;

import java.util.Locale;

/**
 * A ParticleEffect class which is effectively just like AbstractDustParticleEffect.
 * I've named this 'VectorParticleEffect' because I might need multiple particles that read data in form of a Vector3f.
 */
public class VectorParticleEffect implements ParticleEffect {

    public static final ParticleEffect.Factory<VectorParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<>() {
        public VectorParticleEffect read(ParticleType<VectorParticleEffect> type, StringReader reader) throws CommandSyntaxException {
            Vector3f vector3f = readColor(reader);
            reader.expect(' ');
            float scale = reader.readFloat();
            reader.expect(' ');
            int duration = reader.readInt();
            return new VectorParticleEffect(type, vector3f, scale, duration);
        }
        public VectorParticleEffect read(ParticleType<VectorParticleEffect> type, PacketByteBuf buf) {
            return new VectorParticleEffect(type, readColor(buf), buf.readFloat(), buf.readInt());
        }
    };

    private final ParticleType<VectorParticleEffect> type;
    private final Vector3f vector;
    private final float scale;
    private final int duration;

    public VectorParticleEffect(ParticleType<VectorParticleEffect> type, Vector3f vector, float scale, int duration) {
        this.vector = vector;
        this.scale = scale;
        this.type = type;
        this.duration = duration;
    }

    public static Vector3f readColor(StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        float f = reader.readFloat();
        reader.expect(' ');
        float g = reader.readFloat();
        reader.expect(' ');
        float h = reader.readFloat();
        return new Vector3f(f, g, h);
    }

    public static Vector3f readColor(PacketByteBuf buf) {
        return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public Vector3f getVector() {
        return this.vector;
    }

    public float getScale() {
        return this.scale;
    }

    public int getDuration() {
        return this.duration;
    }
    
    @Override
    public ParticleType<?> getType() {
        return this.type;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeFloat(this.vector.x());
        buf.writeFloat(this.vector.y());
        buf.writeFloat(this.vector.z());
        buf.writeFloat(this.scale);
        buf.writeInt(this.duration);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT,
                "%s %.2f %.2f %.2f %.2f %d",
                Registries.PARTICLE_TYPE.getId(this.type),
                this.vector.x(),
                this.vector.y(),
                this.vector.z(),
                this.scale,
                this.duration
        );
    }
}
