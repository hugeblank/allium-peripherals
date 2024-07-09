package dev.hugeblank;

import org.slf4j.Logger;
import dev.hugeblank.peripherals.chatmodem.block.CalibratedChatModemBlock;
import dev.hugeblank.peripherals.chatmodem.block.RangedChatModemBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Material;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.slf4j.LoggerFactory;

import static dev.hugeblank.peripherals.chatmodem.IChatCatcher.CHAT_MODEM_MAX_RANGE;

public class Allium implements ModInitializer {
    public static final FabricLoader FL_INSTANCE = FabricLoader.getInstance();

    public static final String MOD_ID = "allium_peripherals";
    public static final Logger LOGGER= LoggerFactory.getLogger(MOD_ID);
    public static void debug(String o) {
        LOGGER.debug(o);

    }

    @Override
    public void onInitialize() {
        AlliumRegistry.registerBlocks();
        AlliumRegistry.registerBlockEntities();
        AlliumRegistry.registerItems();
    }

    public interface Blocks {
        CalibratedChatModemBlock CHAT_MODEM = new CalibratedChatModemBlock(
            FabricBlockSettings.of(Material.STONE).hardness(2)
        );

        RangedChatModemBlock RANGED_CHAT_MODEM = new RangedChatModemBlock(
            FabricBlockSettings.of(Material.STONE).hardness(2)
        )
            .listenRange(Box.of(Vec3d.ZERO, CHAT_MODEM_MAX_RANGE * 2 + 1, CHAT_MODEM_MAX_RANGE * 2 + 1, CHAT_MODEM_MAX_RANGE * 2 + 1))
            .sendRange(Box.of(Vec3d.ZERO, CHAT_MODEM_MAX_RANGE * 2 + 1, CHAT_MODEM_MAX_RANGE * 2 + 1, CHAT_MODEM_MAX_RANGE * 2 + 1));

        CalibratedChatModemBlock CHAT_MODEM_CREATIVE = new CalibratedChatModemBlock(
            FabricBlockSettings.copyOf(net.minecraft.block.Blocks.BEDROCK)
        );
    }
}
