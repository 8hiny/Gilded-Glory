package shiny.gildedglory.common.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import shiny.gildedglory.common.network.ModPackets;
import shiny.gildedglory.common.network.TestRenderS2CPacket;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TestRenderObjectCommand {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) ->
                dispatcher.register(literal("testrender")
                        .then(argument("operation", StringArgumentType.string())
                                .suggests((ctx, builder) -> CommandSource.suggestMatching(addOrRemove(), builder))
                                .executes(ctx -> {
                                    if (StringArgumentType.getString(ctx, "operation").equals("clear")) {
                                        return clear(ctx.getSource());
                                    }
                                    return 0;
                                })
                                .then(argument("pos", Vec3ArgumentType.vec3())
                                        .executes(ctx -> add(
                                                ctx.getSource(), Vec3ArgumentType.getVec3(ctx, "pos")
                                        ))
                                ))));
    }

    public static int add(ServerCommandSource source, Vec3d pos) {
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            ModPackets.GILDED_GLORY_CHANNEL.sendToClient(new TestRenderS2CPacket(pos, false), player);
            return 1;
        }
        return 0;
    }

    public static int clear(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            ModPackets.GILDED_GLORY_CHANNEL.sendToClient(new TestRenderS2CPacket(Vec3d.ZERO, true), player);
            return 1;
        }
        return 0;
    }

    private static List<String> addOrRemove() {
        List<String> strings = new ArrayList<>();
        strings.add("add");
        strings.add("clear");
        return strings;
    }
}
