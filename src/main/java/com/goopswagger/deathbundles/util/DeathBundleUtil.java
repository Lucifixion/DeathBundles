package com.goopswagger.deathbundles.util;

import com.goopswagger.deathbundles.ext.DeathBundleItemExt;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DeathBundleUtil {

    public static final String DEATHBUNDLE_KEY = "DeathBundle";

    public static ItemStack createDeathBundle() {
        ItemStack bundleStack = new ItemStack(Items.BUNDLE);
        NbtCompound nbtCompound = bundleStack.getOrCreateNbt();
        nbtCompound.putBoolean(DEATHBUNDLE_KEY, true);
        nbtCompound.putInt("HideFlags", 32);
        NbtList enchantmentList = new NbtList();
        enchantmentList.add(new NbtCompound());
        nbtCompound.put(ItemStack.ENCHANTMENTS_KEY, enchantmentList);
        bundleStack.setCustomName(Text.literal("Death Bundle").fillStyle(Style.EMPTY.withItalic(false)));
        return bundleStack;
    }

    public static boolean isDeathBundle(ItemStack stack) {
        return stack.getItem() instanceof BundleItem && ((DeathBundleItemExt) stack.getItem()).isDeathBundle(stack.getNbt());
    }

}
