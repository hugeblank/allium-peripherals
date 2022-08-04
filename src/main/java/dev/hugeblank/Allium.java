package dev.hugeblank;

import dev.hugeblank.peripherals.chatmodem.BlockChatModem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Material;

public class Allium implements ModInitializer {
    public static final FabricLoader FL_INSTANCE = FabricLoader.getInstance();

    public static final String MOD_ID = "allium_peripherals";

    @Override
    public void onInitialize()
    {
        AlliumRegistry.registerBlocks();
        AlliumRegistry.registerBlockEntities();
        AlliumRegistry.registerItems();
    }

    public static void debug(Object o) {
        if (FL_INSTANCE.isDevelopmentEnvironment()) System.out.println(o);
    }

    public static final class Blocks {
        public static final BlockChatModem CHAT_MODEM = new BlockChatModem(
                FabricBlockSettings.of(Material.STONE).hardness(2)
        );

        public static final BlockChatModem CHAT_MODEM_CREATIVE = new BlockChatModem(
                FabricBlockSettings.copyOf(net.minecraft.block.Blocks.BEDROCK)
        );
    }
}
