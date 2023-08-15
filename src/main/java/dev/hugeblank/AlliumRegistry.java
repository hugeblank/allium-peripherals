package dev.hugeblank;

import dev.hugeblank.peripherals.chatmodem.ChatModemBlockEntity;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class AlliumRegistry {
    private static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder
            .create(new Identifier(Allium.MOD_ID, "main"))
            .icon(() -> new ItemStack(Blocks.ALLIUM))
            .build();

    private static final Item.Settings SETTINGS = new Item.Settings().group(ITEM_GROUP);

    public static void registerBlocks() {
        Registry.register(Registry.BLOCK, new Identifier(Allium.MOD_ID, "chat_modem"), Allium.Blocks.CHAT_MODEM);
        Registry.register(Registry.BLOCK, new Identifier(Allium.MOD_ID, "chat_modem_creative"), Allium.Blocks.CHAT_MODEM_CREATIVE);
    }

    public static void registerBlockEntities() {
        ChatModemBlockEntity.CHAT_MODEM_TYPE = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(Allium.MOD_ID, "chat_modem"),
                AlliumRegistry.create(
                        ChatModemBlockEntity::new,
                        Allium.Blocks.CHAT_MODEM,
                        Allium.Blocks.CHAT_MODEM_CREATIVE
                )
        );

    }

    public static <T extends BlockEntity> BlockEntityType<T> create(FabricBlockEntityTypeBuilder.Factory<T> supplier, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(supplier, blocks).build(null);
    }

    private static void registerBlockItem(BlockItem item) {
        Registry.register(Registry.ITEM, Registry.BLOCK.getId(item.getBlock()), item);
    }

    public static void registerItems() {
        registerBlockItem(new BlockItem( Allium.Blocks.CHAT_MODEM, SETTINGS) );
        registerBlockItem(new BlockItem( Allium.Blocks.CHAT_MODEM_CREATIVE, SETTINGS) );
    }
}
