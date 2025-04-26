package shiny.gildedglory.client.particle.effect;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.*;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3f;

public class ColoredParticleEffect extends AbstractDustParticleEffect {

    public static final ParticleEffect.Factory<ColoredParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<>() {

        public ColoredParticleEffect read(ParticleType<ColoredParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            Vector3f vector3f = AbstractDustParticleEffect.readColor(stringReader);
            stringReader.expect(' ');
            float f = stringReader.readFloat();
            return new ColoredParticleEffect(particleType, vector3f, f);
        }

        public ColoredParticleEffect read(ParticleType<ColoredParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            return new ColoredParticleEffect(particleType, AbstractDustParticleEffect.readColor(packetByteBuf), packetByteBuf.readFloat());
        }
    };

    private final ParticleType<ColoredParticleEffect> type;

    public ColoredParticleEffect(ParticleType<ColoredParticleEffect> type, Vector3f color, float scale) {
        super(color, scale);
        this.type = type;
    }
    
    @Override
    public ParticleType<?> getType() {
        return this.type;
    }
}
