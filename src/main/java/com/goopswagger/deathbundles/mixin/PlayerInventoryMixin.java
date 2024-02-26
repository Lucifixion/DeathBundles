package com.goopswagger.deathbundles.mixin;

import com.goopswagger.deathbundles.DeathBundles;
import com.goopswagger.deathbundles.compat.TrinketCompatUtil;
import com.goopswagger.deathbundles.item.DeathBundleItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerEntity.class)
public class PlayerInventoryMixin {
	@Shadow @Final private PlayerInventory inventory;

	@Inject(at = @At("HEAD"), method = "dropInventory", cancellable = true)
	private void init(CallbackInfo ci) {
		PlayerEntity player = (((PlayerEntity) (Object) this));
		if (!player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
			ItemStack stack = new ItemStack(DeathBundles.DEATH_BUNDLE);
			for (List<ItemStack> list : this.inventory.combinedInventory) {
				for (ItemStack itemStack : list) {
					if (!itemStack.isEmpty()) {
						if (itemStack.getItem() == DeathBundles.DEATH_BUNDLE) {
							DeathBundleItem.dropAllBundledItems(itemStack, player);
						} else {
							DeathBundleItem.addToBundle(stack, itemStack);
						}
					}
				}
			}
			if (FabricLoader.getInstance().isModLoaded("trinkets"))
				TrinketCompatUtil.handleTrinkets(player, stack);
			if (DeathBundleItem.getBundleOccupancy(stack) != 0)
				player.dropItem(stack, true, false);
		}
		ci.cancel();
	}
}