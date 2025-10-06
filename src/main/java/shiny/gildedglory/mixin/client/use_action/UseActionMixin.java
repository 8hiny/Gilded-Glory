package shiny.gildedglory.mixin.client.use_action;

import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import shiny.gildedglory.client.use_action.BaseUseAction;

@Mixin(UseAction.class)
public abstract class UseActionMixin implements BaseUseAction {

    @Override
    public Value value() {
        return Value.VANILLA;
    }
}
