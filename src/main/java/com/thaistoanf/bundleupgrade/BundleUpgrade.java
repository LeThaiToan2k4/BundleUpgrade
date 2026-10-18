package com.thaistoanf.bundleupgrade;

import com.thaistoanf.bundleupgrade.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleUpgrade implements ModInitializer {
    public static final String MOD_ID = "bundleupgrade";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.registerModItems();
        LOGGER.info("Bundle Upgrade initialized successfully!");
    }
}
