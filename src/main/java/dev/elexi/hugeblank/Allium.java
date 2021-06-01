package dev.elexi.hugeblank;

import dev.elexi.hugeblank.peripherals.chatmodem.BlockChatModem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Material;
import net.minecraft.util.registry.Registry;

public class Allium implements ModInitializer {
    public static final FabricLoader FL_INSTANCE = FabricLoader.getInstance();

    public static final String MOD_ID = "allium";
    public static final class Blocks {
        public static BlockChatModem chatModem = new BlockChatModem(
                FabricBlockSettings.of(Material.STONE).hardness(2),
                false);

        public static BlockChatModem chatModemCreative = new BlockChatModem(
                FabricBlockSettings.of(Material.STONE).hardness(2),
                true);

        public Blocks() {}
    }

    @Override
    public void onInitialize()
    {
        AlliumRegistry.registerBlocks();
        AlliumRegistry.registerBlockEntities();
        AlliumRegistry.registerItems( Registry.ITEM );
    }

    public static void debug(Object o) {
        if (FL_INSTANCE.isDevelopmentEnvironment()) System.out.println(o);
    }
}
