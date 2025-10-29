package shiny.gildedglory.client.use_action;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.common.item.custom.SheathableWeapon;
import shiny.gildedglory.common.registry.ModRegistries;

public class CustomUseActions {

    public static final CustomUseAction SHEATH = register("sheath", new CustomUseAction(
            context -> {
                AbstractClientPlayerEntity player = context.player();
                ItemStack stack = context.stack();
                boolean bl = stack.getItem() instanceof SheathableWeapon;

                if (player.getItemUseTime() < (bl ? ((SheathableWeapon) stack.getItem()).sheathTime() : 10)) {
                    MatrixStack matrices = context.matrices();
                    float f = (float) stack.getMaxUseTime(player) - ((float) player.getItemUseTimeLeft() - context.tickDelta() + 1.0f);

                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90f));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
                    matrices.translate(-0.01, 0.1, -0.25);

                    //Render sheath
                    if (bl) {
                        matrices.push();
                        matrices.translate(0.0025, 0.7 + 0.05 * (1 - f), 0.16 + 0.01 * (1 - f));
                        matrices.scale(1f, 1.1f, 1.1f);

                        ItemStack sheath = new ItemStack(((SheathableWeapon) stack.getItem()).getSheath(stack));
                        if (stack.hasGlint()) sheath.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
                        context.renderer().renderItem(
                                player,
                                sheath,
                                context.leftHanded() ? ModelTransformationMode.FIRST_PERSON_LEFT_HAND : ModelTransformationMode.FIRST_PERSON_RIGHT_HAND,
                                context.leftHanded(),
                                matrices,
                                context.vertexConsumers(),
                                context.light()
                        );
                        matrices.pop();
                    }
                }
                return stack;
            },
            false
    ));
    public static final CustomUseAction AURADEUS = register("auradeus", new CustomUseAction(
            context -> {
                AbstractClientPlayerEntity player = context.player();
                ItemStack stack = context.stack();
                MatrixStack matrices = context.matrices();

                float f = (float) stack.getMaxUseTime(player) - ((float) player.getItemUseTimeLeft() - context.tickDelta() + 1.0f);
                float g = f / 15.0f;
                g = Math.min(1.0f, (g * g + g * 2.0f) / 3.0f);

                if (g > 0.1f) {
                    float h = MathHelper.sin((f - 0.1f) * 1.3f) * g - 0.1f;
                    matrices.translate(h * 0.0f, h * 0.004f, h * 0.0f);
                }
                matrices.translate(0.0f, g * 0.04f, 0.0f);

                return stack;
            },
            false
    ));

    public static CustomUseAction register(String name, CustomUseAction useAction) {
        return Registry.register(ModRegistries.CUSTOM_USE_ACTION, GildedGlory.id(name), useAction);
    }

    public static void registerCustomUseActions() {
    }
}
