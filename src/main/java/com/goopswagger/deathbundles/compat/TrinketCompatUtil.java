package com.goopswagger.deathbundles.compat;

import com.goopswagger.deathbundles.item.DeathBundleItem;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class TrinketCompatUtil {

    public static void handleTrinkets(PlayerEntity player, ItemStack bundle) {
        TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> trinkets.forEach((ref, trinketStack) -> {
            if (!trinketStack.isEmpty()) {
                TrinketEnums.DropRule dropRule = TrinketsApi.getTrinket(trinketStack.getItem()).getDropRule(trinketStack, ref, player);
                dropRule = TrinketDropCallback.EVENT.invoker().drop(dropRule, trinketStack, ref, player);
                TrinketInventory inventory = ref.inventory();

                if (dropRule == TrinketEnums.DropRule.DEFAULT) {
                    dropRule = inventory.getSlotType().getDropRule();
                }

                if (dropRule == TrinketEnums.DropRule.DEFAULT) {
                    if (EnchantmentHelper.hasVanishingCurse(trinketStack)) {
                        dropRule = TrinketEnums.DropRule.DESTROY;
                    } else {
                        dropRule = TrinketEnums.DropRule.DROP;
                    }
                }

                if (Objects.requireNonNull(dropRule) == TrinketEnums.DropRule.DROP) {
                    DeathBundleItem.addToBundle(bundle, trinketStack);
                }
                ref.inventory().setStack(ref.index(), ItemStack.EMPTY);
            }
        }));
    }

}
