package dev.elexi.hugeblank;

import dev.elexi.hugeblank.peripherals.chatmodem.ChatModemBlockEntity;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public final class AlliumRegistry {

    public static Supplier<ChatModemBlockEntity> normalSupplier;
    public static Supplier<ChatModemBlockEntity> creativeSupplier;

    private static final ItemGroup mainItemGroup = FabricItemGroupBuilder
            .create(new Identifier(Allium.MOD_ID, "main"))
            .icon(() -> new ItemStack(Blocks.ALLIUM))
            .build();

    private AlliumRegistry() {
    }

    public static void registerBlocks() {

        Registry.register(Registry.BLOCK, new Identifier(Allium.MOD_ID, "chat_modem"), Allium.Blocks.chatModem);
        Registry.register(Registry.BLOCK, new Identifier(Allium.MOD_ID, "chat_modem_creative"), Allium.Blocks.chatModemCreative);
    }

    public static void registerTileEntities() {
        normalSupplier = () -> {
            ChatModemBlockEntity be = new ChatModemBlockEntity(ChatModemBlockEntity.normalChatModem, false);
            return be;
        };
        creativeSupplier = () -> {
            ChatModemBlockEntity be = new ChatModemBlockEntity(ChatModemBlockEntity.creativeChatModem, true);
            return be;
        };

        Registry.register(Registry.BLOCK_ENTITY, new Identifier(Allium.MOD_ID, "chat_modem"), ChatModemBlockEntity.normalChatModem);
        Registry.register(Registry.BLOCK_ENTITY, new Identifier(Allium.MOD_ID, "chat_modem_creative"), ChatModemBlockEntity.creativeChatModem);


    }

    private static void registerItemBlock(MutableRegistry<Item> registry, BlockItem item) {
        registry.add(Registry.BLOCK.getId(item.getBlock()), item);
    }

    private static Item.Settings defaultItem() {
        return new Item.Settings().group(mainItemGroup);
    }

    public static void registerItems(MutableRegistry<Item> registry) {
        registerItemBlock( registry, new BlockItem( Allium.Blocks.chatModem, defaultItem() ) );
        registerItemBlock( registry, new BlockItem( Allium.Blocks.chatModemCreative, defaultItem() ) );
    }
}
