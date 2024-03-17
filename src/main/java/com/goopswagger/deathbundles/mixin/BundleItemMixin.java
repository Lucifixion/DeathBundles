package com.goopswagger.deathbundles.mixin;

import com.goopswagger.deathbundles.ext.DeathBundleItemExt;
import com.goopswagger.deathbundles.util.DeathBundleUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static com.goopswagger.deathbundles.util.DeathBundleUtil.DEATHBUNDLE_KEY;

@Mixin(value = BundleItem.class, priority = 9999)
public abstract class BundleItemMixin extends Item implements DeathBundleItemExt {

    @Shadow public abstract void playRemoveOneSound(Entity entity);

    @Shadow
    public static Optional<ItemStack> removeFirstStack(ItemStack stack) {
        return null;
    }

    @Shadow
    public static int addToBundle(ItemStack bundle, ItemStack stack) {
        return 0;
    }

    @Shadow
    public static int getBundleOccupancy(ItemStack stack) {
        return 0;
    }

    public BundleItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "onStackClicked", cancellable = true)
    public void db_onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (!DeathBundleUtil.isDeathBundle(stack))
            return;
        if (clickType == ClickType.RIGHT) {
            ItemStack itemStack = slot.getStack();
            if (itemStack.isEmpty()) {
                this.playRemoveOneSound(player);
                removeFirstStack(stack).ifPresent((removedStack) -> addToBundle(stack, slot.insertStack(removedStack)));
                if (getBundleOccupancy(stack) == 0) {
                    stack.decrement(1);
                }
                cir.setReturnValue(true);
                return;
            }
        }
        cir.setReturnValue(false);
    }

    @Inject(at = @At("HEAD"), method = "onClicked", cancellable = true)
    public void db_onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (!DeathBundleUtil.isDeathBundle(stack))
            return;
        if (clickType == ClickType.RIGHT && otherStack.isEmpty()) {
            BundleItem.removeFirstStack(stack).ifPresent(itemStack -> {
                this.playRemoveOneSound(player);
                cursorStackReference.set(itemStack);
                if (getBundleOccupancy(stack) == 0) {
                    stack.decrement(1);
                }
            });
            cir.setReturnValue(true);
            return;
        }
        cir.setReturnValue(false);
    }

    @Inject(at = @At("HEAD"), method = "addToBundle", cancellable = true)
    private static void db_addToBundle(ItemStack bundle, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!DeathBundleUtil.isDeathBundle(bundle))
            return;
        if (!stack.isEmpty()) {
            NbtCompound nbtCompound = bundle.getOrCreateNbt();
            if (!nbtCompound.contains("Items")) {
                nbtCompound.put("Items", new NbtList());
            }

            int count = stack.getCount();
            if (count == 0) {
                cir.setReturnValue(0);
            } else {
                NbtList nbtList = nbtCompound.getList("Items", 10);
                ItemStack itemStack2 = stack.copyWithCount(count);
                NbtCompound nbtCompound3 = new NbtCompound();
                itemStack2.writeNbt(nbtCompound3);
                nbtList.add(0, nbtCompound3);
                cir.setReturnValue(count);
            }
        } else {
            cir.setReturnValue(0);
        }
    }

    @Inject(at = @At("RETURN"), method = "use")
    public void db_isDeathBundle(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!DeathBundleUtil.isDeathBundle(user.getStackInHand(hand)))
            return;
        user.getStackInHand(hand).decrement(1);
    }

    @Override
    public boolean isDeathBundle(NbtCompound nbtCompound) {
        return nbtCompound != null && nbtCompound.contains(DEATHBUNDLE_KEY) && nbtCompound.getBoolean(DEATHBUNDLE_KEY);
    }
}
