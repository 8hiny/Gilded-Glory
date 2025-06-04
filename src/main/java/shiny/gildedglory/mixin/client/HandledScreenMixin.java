package shiny.gildedglory.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shiny.gildedglory.client.util.StupidSheathedInventoryModelPredicateHelper;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T>, StupidSheathedInventoryModelPredicateHelper {

    @Shadow @Nullable protected Slot focusedSlot;
    @Shadow @Final protected T handler;
    @Unique private ItemStack hoveredStack = ItemStack.EMPTY;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/gui/DrawContext;II)V"))
    private void gildedglory$captureFocusedItem(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.hoveredStack = null;
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            this.hoveredStack = this.focusedSlot.getStack();
        }
        else if (!this.handler.getCursorStack().isEmpty()) {
            this.hoveredStack = this.handler.getCursorStack();
        }
    }

    @Override
    public ItemStack getHoveredStack() {
        return this.hoveredStack;
    }
}
