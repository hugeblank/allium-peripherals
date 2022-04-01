package dev.elexi.hugeblank;

import dev.elexi.hugeblank.peripherals.chatmodem.ChatModemBlockEntity;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public final class AlliumRegistry {
    private static final ItemGroup mainItemGroup = FabricItemGroupBuilder
            .create(new Identifier(Allium.MOD_ID, "main"))
            .icon(() -> new ItemStack(Blocks.ALLIUM))
            .build();

    private static final Item.Settings setting = new Item.Settings().group(mainItemGroup);

    private AlliumRegistry() {
    }

    public static void registerBlocks() {

        Registry.register(Registry.BLOCK, new Identifier(Allium.MOD_ID, "chat_modem"), Allium.Blocks.chatModem);
        Registry.register(Registry.BLOCK, new Identifier(Allium.MOD_ID, "chat_modem_creative"), Allium.Blocks.chatModemCreative);
    }

    public static void registerBlockEntities() {
        ChatModemBlockEntity.registryCreativeMode = false;
        ChatModemBlockEntity.normalChatModem = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Allium.MOD_ID, "chat_modem"), AlliumRegistry.create(ChatModemBlockEntity::new, Allium.Blocks.chatModem));

        ChatModemBlockEntity.registryCreativeMode = true;
        ChatModemBlockEntity.creativeChatModem = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Allium.MOD_ID, "chat_modem_creative"), AlliumRegistry.create(ChatModemBlockEntity::new, Allium.Blocks.chatModemCreative));

        ChatModemBlockEntity.registryCreativeMode = false;
    }

    public static <T extends BlockEntity> BlockEntityType<T> create(FabricBlockEntityTypeBuilder.Factory<T> supplier, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(supplier, blocks).build(null);
    }

    private static void registerItemBlock(MutableRegistry<Item> registry, BlockItem item) {
        Registry.register(registry, Registry.BLOCK.getId(item.getBlock()), item);
    }

    public static void registerItems(MutableRegistry<Item> registry) {
        registerItemBlock( registry, new BlockItem( Allium.Blocks.chatModem, setting ) );
        registerItemBlock( registry, new BlockItem( Allium.Blocks.chatModemCreative, setting ) );
    }
}
