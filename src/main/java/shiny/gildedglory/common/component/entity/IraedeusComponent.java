package shiny.gildedglory.common.component.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import shiny.gildedglory.GildedGlory;
import shiny.gildedglory.GildedGloryClient;
import shiny.gildedglory.common.entity.HomingTarget;
import shiny.gildedglory.common.entity.IraedeusEntity;
import shiny.gildedglory.common.network.ModPackets;
import shiny.gildedglory.common.network.UpdateTargetingC2SPacket;
import shiny.gildedglory.common.network.UpdateReturningC2SPacket;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.util.GildedGloryUtil;

import java.util.UUID;

public class IraedeusComponent implements AutoSyncedComponent, ServerTickingComponent {

    private final PlayerEntity provider;
    public boolean targeting;
    public boolean returning;
    public int targetTime;
    public int returnTime;
    public int slot = -1;
    private ItemStack stack = ItemStack.EMPTY;
    private UUID iraedeusId;

    public IraedeusComponent(PlayerEntity provider) {
        this.provider = provider;
    }

    public static void clientTick(MinecraftClient client) {
        PlayerEntity player = client.player;
        ModComponents.IRAEDEUS.maybeGet(player).ifPresent(component -> {
            boolean targeting = GildedGloryClient.targetIraedeus.isPressed();
            boolean returning = GildedGloryClient.returnIraedeus.isPressed();

            if (targeting) component.targetTime++;
            else component.targetTime = 0;
            if (returning) component.returnTime++;
            else component.returnTime = 0;

            if (targeting != component.targeting) {
                ModPackets.GILDED_GLORY_CHANNEL.sendToServer(new UpdateTargetingC2SPacket(targeting));
                component.targeting = targeting;
            }
            if (returning != component.returning) {
                ModPackets.GILDED_GLORY_CHANNEL.sendToServer(new UpdateReturningC2SPacket(returning));
                component.returning = returning;
            }
        });
    }

    //WHY ARE YOU REMOVING THE ITEMSTACK ON THE SERVER AFTER SETTING IT????? WHAT THE FUCK
    @Override
    public void serverTick() {
        GildedGlory.LOGGER.info("Is summoned: " + this.isSummoned() + " stack: " + this.stack);
        if (this.isSummoned()) {
            if (this.targeting) {
                GildedGlory.LOGGER.info("Setting target from summoned state!");
                Vec3d pos = new Vec3d(this.provider.getX(), this.provider.getEyeY() - 0.25f, this.provider.getZ());
                HomingTarget target = this.handleTarget(pos.add(this.provider.getRotationVector().multiply(48.0)));

                IraedeusEntity iraedeus = new IraedeusEntity(this.provider.getWorld(), this.provider, this.slot, pos.x, pos.y, pos.z);
                iraedeus.setItem(this.stack);
                iraedeus.setTarget(target);

                this.provider.getWorld().spawnEntity(iraedeus);
                this.setEntity(iraedeus.getUuid());

                this.setSummoned(ItemStack.EMPTY);
            }
            else if (this.returning) {
                GildedGlory.LOGGER.info("Returning from summoned state!");
                this.returnStack();
            }
        }
    }

    public void setSlot(int slot) {
        this.slot = slot;
        ModComponents.IRAEDEUS.sync(this.provider);
    }

    public void setEntity(UUID entityId) {
        this.iraedeusId = entityId;
        ModComponents.IRAEDEUS.sync(this.provider);
    }

    public void reset() {
        GildedGlory.LOGGER.info("Resetting!");
        this.targeting = false;
        this.returning = false;
        this.slot = -1;
        this.iraedeusId = null;
        this.stack = ItemStack.EMPTY;
        ModComponents.IRAEDEUS.sync(this.provider);
    }

    public void setSummoned(ItemStack stack) {
        GildedGlory.LOGGER.info("Setting stack to: " + stack);
        this.stack = stack;
        ModComponents.IRAEDEUS.sync(this.provider);
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public boolean isSummoned() {
        return this.stack != null && !this.stack.isEmpty();
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.slot = tag.getInt("Slot");

        this.stack = ItemStack.fromNbt(tag.getCompound("Item"));
        GildedGlory.LOGGER.info("Stack on read: " + this.stack + " is client: " + this.provider.getWorld().isClient());
        if (tag.contains("Entity")) this.iraedeusId = tag.getUuid("Entity");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("Slot", this.slot);

        GildedGlory.LOGGER.info("Stack on write: " + this.stack + " is client: " + this.provider.getWorld().isClient());
        tag.put("Item", this.stack.writeNbt(new NbtCompound()));
        if (this.iraedeusId != null) tag.putUuid("Entity", this.iraedeusId);
    }

    public IraedeusEntity findEntity() {
        Entity entity = GildedGloryUtil.getEntityFromUuid(this.iraedeusId, this.provider.getWorld());
        if (entity instanceof IraedeusEntity iraedeus && iraedeus.getOwner() == this.provider) {
            return iraedeus;
        }
        return null;
    }

    public HomingTarget handleTarget(Vec3d targetPos) {
        Vec3d direction = targetPos.subtract(this.provider.getEyePos());
        Box box = this.provider.getBoundingBox().stretch(direction).expand(1.0, 1.0, 1.0);

        EntityHitResult entityHit = ProjectileUtil.raycast(this.provider, this.provider.getEyePos(), targetPos, box, entity -> entity instanceof LivingEntity && entity != this.provider, direction.lengthSquared());
        BlockHitResult blockHit = this.provider.getWorld().raycast(new RaycastContext(this.provider.getEyePos(), targetPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this.provider));

        if (entityHit == null) {
            if (blockHit.getType() == HitResult.Type.MISS) {
                return new HomingTarget(blockHit.getPos());
            }
            else {
                return new HomingTarget(blockHit.getBlockPos().toCenterPos().add(Vec3d.of(blockHit.getSide().getVector())));
            }
        }
        else {
            return new HomingTarget(entityHit.getEntity());
        }
    }

    public void returnStack() {
        boolean bl;
        if (this.provider.getInventory().getStack(this.slot).isEmpty()) {
            bl = this.provider.getInventory().insertStack(this.slot, this.getStack());
        }
        else {
            bl = this.provider.getInventory().insertStack(this.getStack());
        }

        if (bl) {
            this.reset();
            this.provider.getItemCooldownManager().set(ModItems.IRAEDEUS, 20);
        }
    }
}
