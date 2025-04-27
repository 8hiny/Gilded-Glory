package shiny.gildedglory.common.component.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import shiny.gildedglory.GildedGloryClient;
import shiny.gildedglory.common.entity.HomingTarget;
import shiny.gildedglory.common.entity.IraedeusEntity;
import shiny.gildedglory.common.network.ModPackets;
import shiny.gildedglory.common.network.UpdateTargetingC2SPacket;
import shiny.gildedglory.common.network.UpdateReturningC2SPacket;
import shiny.gildedglory.common.registry.component.ModComponents;
import shiny.gildedglory.common.registry.entity.ModEntities;
import shiny.gildedglory.common.registry.item.ModItems;
import shiny.gildedglory.common.util.GildedGloryUtil;

import java.util.UUID;

public class IraedeusComponent implements AutoSyncedComponent, ServerTickingComponent {

    private final PlayerEntity provider;
    private final Inventory stackHolder = new SimpleInventory(1);
    private UUID iraedeusId;
    public int slot = -1;
    private boolean summoned;
    public boolean targeting;
    public boolean returning;
    public int targetTime;
    public int returnTime;
    public int targetCooldown;

    public IraedeusComponent(PlayerEntity provider) {
        this.provider = provider;
    }

    public static void clientTick(MinecraftClient client) {
        PlayerEntity player = client.player;
        ModComponents.IRAEDEUS.maybeGet(player).ifPresent(component -> {
            boolean targeting = GildedGloryClient.targetIraedeus.isPressed() && component.targetCooldown == 0;
            boolean returning = GildedGloryClient.returnIraedeus.isPressed();

            if (targeting) component.targetTime++;
            else component.targetTime = 0;
            if (returning) component.returnTime++;
            else component.returnTime = 0;
            if (component.targetCooldown > 0) component.targetCooldown--;

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

    @Override
    public void serverTick() {
        if (this.isSummoned() && this.provider.getInventory().selectedSlot == this.slot) {
            if (this.targeting) {
                Vec3d pos = GildedGloryUtil.getThrowPos(this.provider, ModEntities.IRAEDEUS);
                HomingTarget target = this.handleTarget(pos.add(this.provider.getRotationVector().multiply(48.0)));

                IraedeusEntity iraedeus = new IraedeusEntity(this.provider.getWorld(), this.provider, this.slot, pos.x, pos.y, pos.z);
                iraedeus.setItem(this.getStack());
                iraedeus.setTargeting(true);
                iraedeus.setTarget(target);

                this.provider.getWorld().spawnEntity(iraedeus);
                this.setEntity(iraedeus.getUuid());

                this.summoned = false;
                this.setStack(ItemStack.EMPTY);
            }
            else if (this.returning) {
                this.returnStack();
            }
        }

        if (this.slot != -1) {
            if (this.iraedeusId != null) {
                Entity entity = GildedGloryUtil.getEntityFromUuid(this.iraedeusId, this.provider.getWorld());
                if (!(entity instanceof IraedeusEntity)) {
                    this.reset();
                }
            }
            else {
                this.reset();
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

    public void setSummoned(boolean value) {
        this.summoned = value;
        ModComponents.IRAEDEUS.sync(this.provider);
    }

    public boolean isSummoned() {
        return this.summoned;
    }

    public ItemStack getStack() {
        return this.stackHolder.getStack(0);
    }

    public void setStack(ItemStack stack) {
        this.stackHolder.setStack(0, stack);
        ModComponents.IRAEDEUS.sync(this.provider);
    }

    public void reset() {
        this.targeting = false;
        this.returning = false;
        this.slot = -1;
        this.iraedeusId = null;
        this.summoned = false;
        this.targetCooldown = 0;
        this.stackHolder.removeStack(0);
        ModComponents.IRAEDEUS.sync(this.provider);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.slot = tag.getInt("Slot");

        this.stackHolder.setStack(0, ItemStack.fromNbt(tag.getCompound("Item")));
        this.summoned = tag.getBoolean("IsSummoned");
        if (tag.contains("Entity")) this.iraedeusId = tag.getUuid("Entity");
        this.targetCooldown = tag.getInt("TargetCooldown");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("Slot", this.slot);

        tag.put("Item", this.stackHolder.getStack(0).copy().writeNbt(new NbtCompound()));
        tag.putBoolean("IsSummoned", this.summoned);
        if (this.iraedeusId != null) tag.putUuid("Entity", this.iraedeusId);

        //GOD THIS STUPID ISSUE (the ItemStack inside this component SOMEHOW decided to DELETE ITSELF FOR NO REASON RIGHT HERE SO I HAVE TO RESET IT TO ITSELF)
        this.stackHolder.setStack(0, ItemStack.fromNbt(tag.getCompound("Item")));
        tag.putInt("TargetCooldown", this.targetCooldown);
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

    public void resetEntity() {
        Entity entity = GildedGloryUtil.getEntityFromUuid(this.iraedeusId, this.provider.getWorld());
        if (entity instanceof IraedeusEntity iraedeus) {
            iraedeus.insertStack(this.provider);
            iraedeus.discard();
        }
    }
}
