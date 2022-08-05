package dev.hugeblank;

import dev.hugeblank.peripherals.chatmodem.ChatModemBlock;
import dev.hugeblank.peripherals.chatmodem.ChatModemBlockEntity;
import dev.hugeblank.peripherals.entangledmodem.EntangledModemBlock;
import dev.hugeblank.peripherals.entangledmodem.EntangledModemBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Allium implements ModInitializer {
    public static final FabricLoader FL_INSTANCE = FabricLoader.getInstance();
    public static final String ID = "allium_peripherals";

    private static final Item.Settings CATEGORY = new Item.Settings().group(
            FabricItemGroupBuilder.create(new Identifier(Allium.ID, "main"))
                    .icon(() -> new ItemStack(net.minecraft.block.Blocks.ALLIUM))
                    .build()
    );

    public static final class Blocks {
        private static final FabricBlockSettings DEFAULT = FabricBlockSettings.of(Material.STONE).hardness(2);

        public static ChatModemBlock CHAT_MODEM;
        public static ChatModemBlock CHAT_MODEM_CREATIVE;
        public static EntangledModemBlock ENTANGLED_MODEM;
        public static EntangledModemBlock ENTANGLED_MODEM_CREATIVE;

        static {
            CHAT_MODEM = new ChatModemBlock(DEFAULT);
            CHAT_MODEM_CREATIVE = new ChatModemBlock(DEFAULT);
            ENTANGLED_MODEM = new EntangledModemBlock(DEFAULT);
            ENTANGLED_MODEM_CREATIVE = new EntangledModemBlock(DEFAULT);
        }
    }

    private void registerModem(String id, Block block) {
        BlockItem item = new BlockItem(block, CATEGORY);
        Identifier newID = new Identifier(ID, id);
        Registry.register(Registry.ITEM, newID, item);
        Registry.register(Registry.BLOCK, newID, block);
    }

    @Override
    public void onInitialize()
    {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(ID, "chat_modem"), ChatModemBlockEntity.TYPE);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(ID, "entangled_modem"), EntangledModemBlockEntity.TYPE);

        registerModem( "chat_modem", Allium.Blocks.CHAT_MODEM);
        registerModem("chat_modem_creative", Allium.Blocks.CHAT_MODEM_CREATIVE);
        registerModem("entangled_modem", Allium.Blocks.ENTANGLED_MODEM);
        registerModem("entangled_modem_creative", Allium.Blocks.ENTANGLED_MODEM_CREATIVE);
    }

    public static void debug(Object o) {
        if (FL_INSTANCE.isDevelopmentEnvironment()) System.out.println(o);
    }
}