package shiny.gildedglory.mixin.client.accessibility;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import shiny.gildedglory.GildedGlory;

@Mixin(PostEffectProcessor.class)
public class EvilAhhMixin {

    @WrapOperation(method = "parsePass", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;ofVanilla(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"))
    private Identifier gildedglory$allowAnyIdentifierForPath(String path, Operation<Identifier> original, @Local(ordinal = 5) String target) {
        Identifier result = Identifier.tryParse(target);
        if (result == null) {
            result = original.call(path);
        }
        else {
            result = Identifier.of(result.getNamespace(), "textures/effect/" + result.getPath() + ".png");
        }
        return result;
    }
}
