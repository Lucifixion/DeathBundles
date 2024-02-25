package com.goopswagger.deathbundles;

import com.goopswagger.deathbundles.item.DeathBundleItem;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeathBundles implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("deathbundles");
	public static final Item DEATH_BUNDLE = new DeathBundleItem(new FabricItemSettings().maxCount(1).fireproof().rarity(Rarity.RARE));

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier("deathbundles", "death_bundle"), DEATH_BUNDLE);
	}
}