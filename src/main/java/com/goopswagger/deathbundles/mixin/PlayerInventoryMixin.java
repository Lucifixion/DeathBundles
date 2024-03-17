package com.goopswagger.deathbundles.mixin;

import com.goopswagger.deathbundles.compat.TrinketCompatUtil;
import com.goopswagger.deathbundles.util.DeathBundleUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = PlayerEntity.class, priority = 9999)
public class PlayerInventoryMixin {
	@Shadow @Final private PlayerInventory inventory;

	@Inject(at = @At("HEAD"), method = "dropInventory")
	private void init(CallbackInfo ci) {
		PlayerEntity player = (((PlayerEntity) (Object) this));
		if (!player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
			ItemStack bundleStack = DeathBundleUtil.createDeathBundle();
			for (List<ItemStack> list : this.inventory.combinedInventory) {
				for (ItemStack itemStack : list) {
					if (!itemStack.isEmpty()) {
						if (DeathBundleUtil.isDeathBundle(itemStack)) {
							BundleItem.dropAllBundledItems(itemStack, player);
						} else {
							BundleItem.addToBundle(bundleStack, itemStack);
							itemStack.decrement(itemStack.getCount());
						}
					}
				}
			}
			if (FabricLoader.getInstance().isModLoaded("trinkets"))
				TrinketCompatUtil.handleTrinkets(player, bundleStack);
			if (BundleItem.getBundleOccupancy(bundleStack) != 0)
				player.dropItem(bundleStack, true, false);
		}
	}
}