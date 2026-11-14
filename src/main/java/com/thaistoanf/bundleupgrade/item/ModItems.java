package com.thaistoanf.bundleupgrade.item;

import com.thaistoanf.bundleupgrade.BundleUpgrade;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModItems {
    public static final Item COPPER_BUNDLE = register("copper_bundle", 74);
    public static final Item IRON_BUNDLE = register("iron_bundle", 94);
    public static final Item GOLD_BUNDLE = register("gold_bundle", 124);
    public static final Item EMERALD_BUNDLE = register("emerald_bundle", 164);
    public static final Item DIAMOND_BUNDLE = register("diamond_bundle", 214);

    private ModItems() {}

    private static Item register(String name, int capacity) {
        Identifier id = Identifier.of(BundleUpgrade.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item.Settings settings = new Item.Settings()
                .registryKey(key)
                .maxCount(1)
                .component(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
        UpgradedBundleItem item = new UpgradedBundleItem(capacity, settings);
        return Registry.register(Registries.ITEM, key, item);
    }

    public static void registerModItems() {
        BundleUpgrade.LOGGER.info("Registering upgraded bundles for {}", BundleUpgrade.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(
                    Items.BUNDLE,
                    COPPER_BUNDLE,
                    IRON_BUNDLE,
                    GOLD_BUNDLE,
                    EMERALD_BUNDLE,
                    DIAMOND_BUNDLE
            );
        });
    }
}
