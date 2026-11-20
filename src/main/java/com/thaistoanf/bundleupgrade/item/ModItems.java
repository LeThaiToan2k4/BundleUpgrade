package com.thaistoanf.bundleupgrade.item;

import com.thaistoanf.bundleupgrade.BundleUpgrade;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;

public final class ModItems {
    public static final Item COPPER_BUNDLE = register("copper_bundle", 74);
    public static final Item IRON_BUNDLE = register("iron_bundle", 94);
    public static final Item GOLD_BUNDLE = register("gold_bundle", 124);
    public static final Item EMERALD_BUNDLE = register("emerald_bundle", 164);
    public static final Item DIAMOND_BUNDLE = register("diamond_bundle", 214);

    private ModItems() {}

    private static Item register(String name, int capacity) {
        Identifier id = Identifier.fromNamespaceAndPath(BundleUpgrade.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        Item.Properties properties = new Item.Properties()
                .setId(key)
                .stacksTo(1)
                .component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        UpgradedBundleItem item = new UpgradedBundleItem(capacity, properties);
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void registerModItems() {
        BundleUpgrade.LOGGER.info("Registering upgraded bundles for {}", BundleUpgrade.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output ->
                output.insertAfter(
                        Items.BUNDLE,
                        COPPER_BUNDLE,
                        IRON_BUNDLE,
                        GOLD_BUNDLE,
                        EMERALD_BUNDLE,
                        DIAMOND_BUNDLE
                )
        );
    }
}
