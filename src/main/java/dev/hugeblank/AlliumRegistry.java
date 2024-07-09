package dev.hugeblank;

import dev.hugeblank.peripherals.chatmodem.ChatModemBlockEntity;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public final class AlliumRegistry {
    private static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder
            .create(alliumId("main"))
            .icon(() -> new ItemStack(Blocks.ALLIUM))
            .build();

    @NotNull
    public static Identifier alliumId(String main) {
        return new Identifier(Allium.MOD_ID, main);
    }

    private static final Item.Settings SETTINGS = new Item.Settings().group(ITEM_GROUP);

    public static void registerBlocks() {
        Registry.register(Registry.BLOCK, alliumId("chat_modem"), Allium.Blocks.CHAT_MODEM);
        //TODO Parsing error loading recipe allium_peripherals:ranged_chat_modem
        Registry.register(Registry.BLOCK, alliumId("ranged_chat_modem"), Allium.Blocks.RANGED_CHAT_MODEM);
        Registry.register(Registry.BLOCK, alliumId("chat_modem_creative"), Allium.Blocks.CHAT_MODEM_CREATIVE);
    }

    public static void registerBlockEntities() {
        ChatModemBlockEntity.CHAT_MODEM_TYPE = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
            alliumId("chat_modem"),
                AlliumRegistry.create(
                        ChatModemBlockEntity::new,
                        Allium.Blocks.CHAT_MODEM,
                        Allium.Blocks.RANGED_CHAT_MODEM,
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
        registerBlockItem(new BlockItem( Allium.Blocks.RANGED_CHAT_MODEM, SETTINGS) );
        registerBlockItem(new BlockItem( Allium.Blocks.CHAT_MODEM_CREATIVE, SETTINGS) );
    }
}
