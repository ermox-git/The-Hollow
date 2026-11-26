package com.thehollow;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheHollow implements ModInitializer {
    public static final String MOD_ID = "thehollow";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEntities.register();
        ModItems.register();
        DreadEvents.register();
        LOGGER.info("The Hollow is awake.");
    }
}
